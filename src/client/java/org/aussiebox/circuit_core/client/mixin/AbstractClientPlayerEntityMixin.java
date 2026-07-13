package org.aussiebox.circuit_core.client.mixin;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.client.CircuitCoreClient;
import org.aussiebox.circuit_core.client.pal.PALControllerHandler;
import org.aussiebox.circuit_core.client.pal.PALHandlerHolder;
import org.aussiebox.circuit_core.pal.handler.HandlerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin implements PALHandlerHolder {
    @Unique private Object2ObjectOpenHashMap<Identifier, PALControllerHandler<? extends HandlerData>> animationHandlers = new Object2ObjectOpenHashMap<>();
    @Unique private UUID uuid;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void circuitCore$getGameProfile(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        if (profile != null) {
            this.uuid = profile.id();
        }
    }

    @Override
    public void circuitCore$setHandler(Identifier id, PALControllerHandler<? extends HandlerData> handler) {
        if (this.animationHandlers == null) this.animationHandlers = new Object2ObjectOpenHashMap<>();

        CircuitCore.LOGGER.info(String.valueOf(uuid));
        for (Map.Entry<Identifier, PALControllerHandler<? extends HandlerData>> entry : animationHandlers.entrySet()) {
            Object2ObjectOpenHashMap<UUID, PALControllerHandler<? extends HandlerData>> map = CircuitCoreClient.handlerRegistry.getOrDefault(entry.getKey(), new Object2ObjectOpenHashMap<>());
            map.put(uuid, entry.getValue());
            CircuitCoreClient.handlerRegistry.put(entry.getKey(), map);
        }

        this.animationHandlers.put(id, handler);
        CircuitCore.LOGGER.info(String.valueOf(CircuitCoreClient.handlerRegistry));
    }

    @Override
    public void circuitCore$setHandlers(Object2ObjectOpenHashMap<Identifier, PALControllerHandler<? extends HandlerData>> handlers) {
        if (this.animationHandlers == null) this.animationHandlers = new Object2ObjectOpenHashMap<>();

        CircuitCore.LOGGER.info(String.valueOf(uuid));
        for (Map.Entry<Identifier, PALControllerHandler<? extends HandlerData>> entry : animationHandlers.entrySet()) {
            Object2ObjectOpenHashMap<UUID, PALControllerHandler<? extends HandlerData>> map = CircuitCoreClient.handlerRegistry.getOrDefault(entry.getKey(), new Object2ObjectOpenHashMap<>());
            map.put(uuid, entry.getValue());
            CircuitCoreClient.handlerRegistry.put(entry.getKey(), map);
        }

        this.animationHandlers.putAll(handlers);
        CircuitCore.LOGGER.info(String.valueOf(CircuitCoreClient.handlerRegistry));
    }

    @Override
    public Object2ObjectOpenHashMap<Identifier, PALControllerHandler<? extends HandlerData>> circuitCore$getHandlers() {
        if (this.animationHandlers == null) this.animationHandlers = new Object2ObjectOpenHashMap<>();

        return this.animationHandlers;
    }
}
