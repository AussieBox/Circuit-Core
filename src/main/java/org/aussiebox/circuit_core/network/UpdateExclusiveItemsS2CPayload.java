package org.aussiebox.circuit_core.network;

import com.mojang.serialization.Codec;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.aussiebox.circuit_core.CircuitCore;

import java.util.List;

public record UpdateExclusiveItemsS2CPayload(List<RegistryKey<Item>> items) implements CustomPayload {
    public static final Id<UpdateExclusiveItemsS2CPayload> ID = new Id<>(CircuitCore.id("update_exclusive_items"));
    public static final PacketCodec<RegistryByteBuf, UpdateExclusiveItemsS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.codec(Codec.list(RegistryKey.createCodec(RegistryKeys.ITEM))), UpdateExclusiveItemsS2CPayload::items,
            UpdateExclusiveItemsS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
