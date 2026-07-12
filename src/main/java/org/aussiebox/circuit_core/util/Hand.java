package org.aussiebox.circuit_core.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum Hand implements StringIdentifiable {
    MAIN_HAND("main_hand"),
    OFF_HAND("off_hand"),
    NONE("none"),
    AUTO("auto");

    public static final Codec<Hand> CODEC = StringIdentifiable.createCodec(Hand::values);
    private final String id;

    Hand(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    public Hand fromId(String id) {
        for (Hand hand : values())
            if (hand.id.matches(id)) return hand;
        return null;
    }
}
