package org.aussiebox.circuit_core.client.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.client.CircuitCoreClient;
import org.aussiebox.circuit_core.client.pal.PALControllerHandler;
import org.aussiebox.circuit_core.client.pal.PendingControllerHandler;
import org.aussiebox.circuit_core.pal.handler.HandlerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    //? >=1.21.10 {
    /*@Inject(method = "onPlayerList", at = @At("HEAD"))
    private void circuitCore$onPlayerAdded(PlayerListS2CPacket packet, CallbackInfo ci) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;
        for (PlayerListS2CPacket.Entry playerEntry : packet.getPlayerAdditionEntries()) {
            PlayerEntity player = world.getPlayerByUuid(playerEntry.profileId());
            if (player == null) continue;
            if (!(player instanceof AbstractClientPlayerEntity playerEntity)) continue;
            if (!CircuitCoreClient.pendingHandlers.containsKey(player.getId())) continue;

            Object2ObjectOpenHashMap<Identifier, PendingControllerHandler<? extends HandlerData>> pendingHandlers = CircuitCoreClient.pendingHandlers.get(player.getId());
            for (PendingControllerHandler<? extends HandlerData> handler : pendingHandlers.values()) {
                Object2ObjectOpenHashMap<UUID, PALControllerHandler<? extends HandlerData>> handlers = CircuitCoreClient.handlerRegistry.getOrDefault(handler.controllerId, new Object2ObjectOpenHashMap<>());
                handlers.put(player.getUuid(), handler.create(playerEntity));
                CircuitCoreClient.handlerRegistry.put(handler.controllerId, handlers);
            }
        }
    }
    *///? }
}
