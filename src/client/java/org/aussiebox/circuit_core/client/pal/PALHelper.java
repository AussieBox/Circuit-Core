package org.aussiebox.circuit_core.client.pal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.CircuitCoreConstants;
import org.aussiebox.circuit_core.client.CircuitCoreClient;

import java.util.UUID;

public class PALHelper {
    /// Sets the animation of the given {@link PALControllerHandler PALControllerHandler} for a specific player on the client.<br>
    /// To set the animation for the given player on multiple clients, send a {@link org.aussiebox.circuit_core.network.SetAnimationS2CPayload SetAnimationS2CPayload} to each client.
    ///
    /// @return Whether the animation was successfully set
    /// @see org.aussiebox.circuit_core.network.SetAnimationS2CPayload
    public static boolean setAnimation(AbstractClientPlayerEntity player, Identifier controller, Identifier animation) {
        if (animation == null) animation = CircuitCoreConstants.NO_ANIMATION;
        PALControllerHandler handler = getHandler(player, controller);
        if (handler == null) return false;
        handler.animation = animation;
        setHandler(handler);
        return true;
    }

    /// Fetches the {@link PALControllerHandler PALControllerHandler} of a specific player for the given controller.
    ///
    /// @return The {@link PALControllerHandler PALControllerHandler} instance, or {@code null}
    public static PALControllerHandler getHandler(AbstractClientPlayerEntity player, Identifier controller) {
        Object2ObjectOpenHashMap<UUID, PALControllerHandler> handlers = CircuitCoreClient.handlerRegistry.getOrDefault(controller, null);
        if (handlers == null) {
            CircuitCore.LOGGER.error("PAL Controller {} was not found", controller.toString());
            return null;
        }
        PALControllerHandler handler = handlers.getOrDefault(player.getUuid(), null);
        if (handler == null) {
            CircuitCore.LOGGER.error("PAL Handler for player {} was not found", player.getNameForScoreboard());
            return null;
        }
        return handler;
    }

    /// Updates the given {@link PALControllerHandler PALControllerHandler} in the handler registry.<br>
    /// You shouldn't need to call this, use {@link PALHelper#setAnimation(AbstractClientPlayerEntity, Identifier, Identifier) setAnimation()} to set animations instead.
    public static void setHandler(PALControllerHandler handler) {
        Object2ObjectOpenHashMap<UUID, PALControllerHandler> handlers = CircuitCoreClient.handlerRegistry.getOrDefault(handler.controllerId, new Object2ObjectOpenHashMap<>());
        handlers.put(handler.player.getUuid(), handler);
        CircuitCoreClient.handlerRegistry.put(handler.controllerId, handlers);
    }
}
