package org.asamk.signal.manager.storage.keyValue;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectedKeyValueStore extends KeyValueStore {
    private final Connection connection;

    public ConnectedKeyValueStore(Connection connection) {
        super(null);
        this.connection = connection;
    }

    public <T> T getEntry(KeyValueEntry<T> key) {
        try {
            return getEntry(connection, key);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> boolean storeEntry(KeyValueEntry<T> key, T value) {
        try {
            return storeEntry(connection, key, value);
        } catch (SQLException e) {
            throw new RuntimeException("Failed update key_value store", e);
        }
    }
}
