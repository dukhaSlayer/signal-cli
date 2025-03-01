package org.asamk.signal.manager.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.whispersystems.signalservice.internal.util.Hex;

import java.io.IOException;
import java.util.Arrays;

public class StickerPackId {

    private final byte[] id;

    private StickerPackId(final byte[] id) {
        this.id = id;
    }

    public static StickerPackId deserialize(byte[] packId) {
        return new StickerPackId(packId);
    }

    public byte[] serialize() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final StickerPackId that = (StickerPackId) o;

        return Arrays.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(id);
    }

    @Override
    public String toString() {
        return "StickerPackId{" + Hex.toStringCondensed(id) + '}';
    }

    @JsonValue
    String toHexString() {
        return Hex.toStringCondensed(id);
    }

    @JsonCreator
    public static StickerPackId fromHexString(String hexString) {
        try {
            return deserialize(Hex.fromStringCondensed(hexString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
