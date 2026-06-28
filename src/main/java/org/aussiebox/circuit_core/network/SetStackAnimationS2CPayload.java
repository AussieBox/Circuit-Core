package org.aussiebox.circuit_core.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import org.aussiebox.circuit_core.CircuitCore;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record SetStackAnimationS2CPayload(UUID playerUUID, Identifier controller, Identifier animation, Optional<ItemStack> stack, String hand) implements CustomPayload {
    public static final Id<SetStackAnimationS2CPayload> ID = new Id<>(CircuitCore.id("set_stack_animation"));
    public static final PacketCodec<RegistryByteBuf, SetStackAnimationS2CPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, SetStackAnimationS2CPayload::playerUUID,
            Identifier.PACKET_CODEC, SetStackAnimationS2CPayload::controller,
            Identifier.PACKET_CODEC, SetStackAnimationS2CPayload::animation,
            PacketCodecs.optional(ItemStack.PACKET_CODEC), SetStackAnimationS2CPayload::stack,
            PacketCodecs.STRING, SetStackAnimationS2CPayload::hand,
            SetStackAnimationS2CPayload::new
    );

    public SetStackAnimationS2CPayload(UUID playerUUID, Identifier controller, Identifier animation, Optional<ItemStack> stack, String hand) {
        this.playerUUID = playerUUID;
        this.controller = controller;
        this.animation = animation;
        this.stack = stack;
        this.hand = hand;

        if (!Objects.equals(hand, "MAIN_HAND") && !Objects.equals(hand, "OFF_HAND") && !Objects.equals(hand, "AUTO") && !Objects.equals(hand, "NULL")) throw new IllegalArgumentException("Hand parameter in SetStackAnimationS2CPayload must be either \"MAIN_HAND\", \"OFF_HAND\", \"AUTO\", or \"NULL\"");
    }

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
