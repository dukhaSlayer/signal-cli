package org.asamk.signal.manager.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Database implements AutoCloseable {

    private final Logger logger;
    private final long databaseVersion;
    private final HikariDataSource dataSource;

    protected Database(final Logger logger, final long databaseVersion, final HikariDataSource dataSource) {
        this.logger = logger;
        this.databaseVersion = databaseVersion;
        this.dataSource = dataSource;
    }

    public static <T extends Database> T initDatabase(
            File databaseFile,
            Function<HikariDataSource, T> newDatabase
    ) throws SQLException {
        HikariDataSource dataSource = null;

        try {
            dataSource = getHikariDataSource(databaseFile.getAbsolutePath());

            final var result = newDatabase.apply(dataSource);
            result.initDb();
            dataSource = null;
            return result;
        } finally {
            if (dataSource != null) {
                dataSource.close();
            }
        }
    }

    record ConnectionInfo(String stacktrace, Long timestamp) {
        ConnectionInfo(String stacktrace) {
            this(stacktrace, System.currentTimeMillis());
        }
        Long age() {
            return System.currentTimeMillis() - timestamp;
        }
    }
    private static final Map<String, ConnectionInfo> OpenConnections = new LinkedHashMap<>();

    /**
     * DS: there are connection deadlocks, lets add some logic to track them.
     * TODO: remove this when client session is stable.
     */
    public final Connection getConnection() throws SQLException {
        var id = UUID.randomUUID().toString();
        var stack = Arrays.stream(Thread.currentThread().getStackTrace()).map(Objects::toString).collect(Collectors.joining(", "));
        logger.trace("getConnection: {} on {}", id, stack);
        OpenConnections.put(id, new ConnectionInfo(stack));
        final Connection connection;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("getConnection() failed for {}, possible deadlock. Open connections (the last one is failed):\n {}",
                id,
                OpenConnections.entrySet().stream().map(kv ->
                    kv.getKey() + ": " + kv.getValue().age() + ": " + kv.getValue().stacktrace()).collect(Collectors.joining("; \n")),
                e);
            throw e;
        }
        return wrap(logger, id, connection);
    }

    public static Connection wrap(Logger logger, String id, Connection originalConnection) {
        return (Connection) Proxy.newProxyInstance(
            originalConnection.getClass().getClassLoader(),
            new Class[]{Connection.class},
            (proxy, method, args) -> {
                if ("close".equals(method.getName())) {
                    logger.trace("Connection.close() for {}", id);
                    OpenConnections.remove(id);
                }
                return method.invoke(originalConnection, args);
            }
        );
    }

    @Override
    public void close() {
        dataSource.close();
    }

    protected final void initDb() throws SQLException {
        try (final var connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            final var userVersion = getUserVersion(connection);
            logger.trace("Current database version: {} Program database version: {}", userVersion, databaseVersion);

            if (userVersion == 0) {
                createDatabase(connection);
                setUserVersion(connection, databaseVersion);
            } else if (userVersion > databaseVersion) {
                logger.error("Database has been updated by a newer signal-cli version");
                throw new SQLException("Database has been updated by a newer signal-cli version");
            } else if (userVersion < databaseVersion) {
                upgradeDatabase(connection, userVersion);
                setUserVersion(connection, databaseVersion);
            }
            connection.commit();
        }
    }

    protected abstract void createDatabase(final Connection connection) throws SQLException;

    protected abstract void upgradeDatabase(final Connection connection, long oldVersion) throws SQLException;

    private static long getUserVersion(final Connection connection) throws SQLException {
        try (final var statement = connection.createStatement()) {
            final var resultSet = statement.executeQuery("PRAGMA user_version");
            return resultSet.getLong(1);
        }
    }

    private static void setUserVersion(final Connection connection, long userVersion) throws SQLException {
        try (final var statement = connection.createStatement()) {
            statement.executeUpdate("PRAGMA user_version = " + userVersion);
        }
    }

    private static HikariDataSource getHikariDataSource(final String databaseFile) {
        final var sqliteConfig = new SQLiteConfig();
        sqliteConfig.setBusyTimeout(60_000);
        sqliteConfig.setTransactionMode(SQLiteConfig.TransactionMode.DEFERRED);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile + "?foreign_keys=ON&journal_mode=wal");
        config.setDataSourceProperties(sqliteConfig.toProperties());
        config.setMinimumIdle(0);
        config.setConnectionTimeout(30_000);
        config.setMaximumPoolSize(1); // this + lower timeouts allows us to find connection issues faster
        config.setMaxLifetime(0);
        return new HikariDataSource(config);
    }
}
