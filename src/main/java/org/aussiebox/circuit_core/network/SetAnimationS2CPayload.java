package org.aussiebox.circuit_core.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import org.aussiebox.circuit_core.CircuitCore;

import java.util.UUID;

public record SetAnimationS2CPayload(UUID playerUUID, Identifier controller, Identifier animation) implements CustomPayload {
    public static final Id<SetAnimationS2CPayload> ID = new Id<>(CircuitCore.id("set_animation"));
    public static final PacketCodec<RegistryByteBuf, SetAnimationS2CPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, SetAnimationS2CPayload::playerUUID,
            Identifier.PACKET_CODEC, SetAnimationS2CPayload::controller,
            Identifier.PACKET_CODEC, SetAnimationS2CPayload::animation,
            SetAnimationS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
