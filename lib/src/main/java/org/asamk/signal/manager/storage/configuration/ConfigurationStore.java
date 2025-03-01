package org.asamk.signal.manager.storage.configuration;

import org.asamk.signal.manager.api.PhoneNumberSharingMode;
import org.asamk.signal.manager.storage.keyValue.KeyValueEntry;
import org.asamk.signal.manager.storage.keyValue.KeyValueStore;
import org.asamk.signal.manager.storage.recipients.RecipientStore;

import java.sql.Connection;
import java.sql.SQLException;

public class ConfigurationStore {

    private final KeyValueStore keyValueStore;
    private final RecipientStore recipientStore;

    private final static KeyValueEntry<Boolean> readReceipts = new KeyValueEntry<>("config-read-receipts", Boolean.class);
    private final static KeyValueEntry<Boolean> unidentifiedDeliveryIndicators = new KeyValueEntry<>(
            "config-unidentified-delivery-indicators",
            Boolean.class);
    private final static KeyValueEntry<Boolean> typingIndicators = new KeyValueEntry<>("config-typing-indicators",
            Boolean.class);
    private final static KeyValueEntry<Boolean> linkPreviews = new KeyValueEntry<>("config-link-previews", Boolean.class);
    private final static KeyValueEntry<Boolean> phoneNumberUnlisted = new KeyValueEntry<>("config-phone-number-unlisted",
            Boolean.class);
    private final static KeyValueEntry<PhoneNumberSharingMode> phoneNumberSharingMode = new KeyValueEntry<>(
            "config-phone-number-sharing-mode",
            PhoneNumberSharingMode.class);
    private final static KeyValueEntry<String> usernameLinkColor = new KeyValueEntry<>("username-link-color", String.class);

    public ConfigurationStore(final KeyValueStore keyValueStore, RecipientStore recipientStore) {
        this.keyValueStore = keyValueStore;
        this.recipientStore = recipientStore;
    }

    public ConnectedConfigurationStore withConnection(final Connection connection) throws SQLException {
        return new ConnectedConfigurationStore(keyValueStore, recipientStore, connection);
    }

    public Boolean getReadReceipts() {
        return keyValueStore.getEntry(readReceipts);
    }

    // DS: thanks to `withConnection()` method we don't need these versions.
    // Commenting them out for now, to simplify merge. Will try to refactor connection management to use context object pattern.

//    public Boolean getReadReceipts(final Connection connection) throws SQLException {
//        return keyValueStore.getEntry(connection, readReceipts);
//    }

    public void setReadReceipts(final boolean value) {
        if (keyValueStore.storeEntry(readReceipts, value)) {
            recipientStore.rotateSelfStorageId();
        }
    }

    public Boolean getUnidentifiedDeliveryIndicators() {
        return keyValueStore.getEntry(unidentifiedDeliveryIndicators);
    }

//    public Boolean getUnidentifiedDeliveryIndicators(final Connection connection) throws SQLException {
//        return keyValueStore.getEntry(connection, unidentifiedDeliveryIndicators);
//    }

    public void setUnidentifiedDeliveryIndicators(final boolean value) {
        if (keyValueStore.storeEntry(unidentifiedDeliveryIndicators, value)) {
            recipientStore.rotateSelfStorageId();
        }
    }

    public Boolean getTypingIndicators() {
        return keyValueStore.getEntry(typingIndicators);
    }

//    public Boolean getTypingIndicators(final Connection connection) throws SQLException {
//        return keyValueStore.getEntry(connection, typingIndicators);
//    }

    public void setTypingIndicators(final boolean value) {
        if (keyValueStore.storeEntry(typingIndicators, value)) {
            recipientStore.rotateSelfStorageId();
        }
    }

    public Boolean getLinkPreviews() {
        return keyValueStore.getEntry(linkPreviews);
    }

//    public Boolean getLinkPreviews(final Connection connection) throws SQLException {
//        return keyValueStore.getEntry(connection, linkPreviews);
//    }

    public void setLinkPreviews(final boolean value) {
        if (keyValueStore.storeEntry(linkPreviews, value)) {
            recipientStore.rotateSelfStorageId();
        }
    }

    public Boolean getPhoneNumberUnlisted() {
        return keyValueStore.getEntry(phoneNumberUnlisted);
    }

//    public Boolean getPhoneNumberUnlisted(final Connection connection) throws SQLException {
//        return keyValueStore.getEntry(connection, phoneNumberUnlisted);
//    }

    public void setPhoneNumberUnlisted(final boolean value) {
        if (keyValueStore.storeEntry(phoneNumberUnlisted, value)) {
            recipientStore.rotateSelfStorageId();
        }
    }

    public PhoneNumberSharingMode getPhoneNumberSharingMode() {
        return keyValueStore.getEntry(phoneNumberSharingMode);
    }

//    public PhoneNumberSharingMode getPhoneNumberSharingMode(final Connection connection) throws SQLException {
//        return keyValueStore.getEntry(connection, phoneNumberSharingMode);
//    }

    public void setPhoneNumberSharingMode(final PhoneNumberSharingMode value) {
        if (keyValueStore.storeEntry(phoneNumberSharingMode, value)) {
            recipientStore.rotateSelfStorageId();
        }
    }

    public String getUsernameLinkColor() {
        return keyValueStore.getEntry(usernameLinkColor);
    }

//    public String getUsernameLinkColor(final Connection connection) throws SQLException {
//        return keyValueStore.getEntry(connection, usernameLinkColor);
//    }

    public void setUsernameLinkColor(final String color) {
        if (keyValueStore.storeEntry(usernameLinkColor, color)) {
            recipientStore.rotateSelfStorageId();
        }
    }
}
