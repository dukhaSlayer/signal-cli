package org.asamk.signal.manager.storage.configuration;

import org.asamk.signal.manager.storage.keyValue.KeyValueStore;
import org.asamk.signal.manager.storage.recipients.RecipientStore;

import java.sql.Connection;

public class ConnectedConfigurationStore extends ConfigurationStore {
    public ConnectedConfigurationStore(KeyValueStore keyValueStore, RecipientStore recipientStore, Connection connection) {
        super(keyValueStore.withConnection(connection), recipientStore.withConnection(connection));
    }
}
