package org.asamk.signal.manager.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Base64;

public abstract sealed class GroupId permits GroupIdV1, GroupIdV2 {

    private final byte[] id;

    public static GroupIdV1 v1(byte[] id) {
        return new GroupIdV1(id);
    }

    public static GroupIdV2 v2(byte[] id) {
        return new GroupIdV2(id);
    }

    public static GroupId unknownVersion(byte[] id) {
        if (id.length == 16) {
            return new GroupIdV1(id);
        } else if (id.length == 32) {
            return new GroupIdV2(id);
        }

        throw new AssertionError("Invalid group id of size " + id.length);
    }

    @JsonCreator
    public static GroupId fromBase64(String id) throws GroupIdFormatException {
        try {
            return unknownVersion(java.util.Base64.getDecoder().decode(id));
        } catch (Throwable e) {
            throw new GroupIdFormatException(id, e);
        }
    }

    protected GroupId(final byte[] id) {
        this.id = id;
    }

    public byte[] serialize() {
        return id;
    }

    @JsonValue
    public String toBase64() {
        return Base64.getEncoder().encodeToString(id);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final var groupId = (GroupId) o;

        return Arrays.equals(id, groupId.id);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(id);
    }
}
