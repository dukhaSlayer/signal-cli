package org.asamk.signal.manager.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public abstract class StoreBase {
    private final Database database;
    private final Connection existingConnection;

    protected StoreBase(Database database, Connection existingConnection) {
        this.database = database;
        this.existingConnection = existingConnection;
        if (database == null && existingConnection == null) {
            throw new IllegalArgumentException("Database or connection is required");
        }
    }

    public record ConnectionHolder(Connection connection, boolean shouldClose) implements Supplier<Connection>, AutoCloseable {
        public ConnectionHolder {
            if (connection == null) {
                throw new IllegalArgumentException("Connection is required");
            }
        }

        @Override
        public void close() throws SQLException {
            if (shouldClose) {
                connection.close();
            }
        }

        @Override
        public Connection get() {
            return connection;
        }
    }

    @FunctionalInterface
    public interface SQLFunction<T> {
        T apply(Connection connection) throws SQLException;
    }

    @FunctionalInterface
    public interface SQLRunnable {
        void run(Connection connection) throws SQLException;
    }


    protected ConnectionHolder getConnectionImpl() throws SQLException {
        return getDatabase() != null
            ? new ConnectionHolder(getDatabase().getConnection(), true)
            : new ConnectionHolder(getExistingConnection(), false);
    }

    protected <T> T withConnection(SQLFunction<T> function) throws SQLException {
        try (final var connection = getConnectionImpl()) {
            return function.apply(connection.get());
        }
    }

    protected void withConnectionRun(SQLRunnable runnable) throws SQLException {
        try (final var connection = getConnectionImpl()) {
            runnable.run(connection.get());
        }
    }

    protected Database getDatabase() {
        return database;
    }

    protected Connection getExistingConnection() {
        return existingConnection;
    }
}
