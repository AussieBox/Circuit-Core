package org.aussiebox.circuit_core.client.pal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.CircuitCoreConstants;
import org.aussiebox.circuit_core.client.CircuitCoreClient;
import org.aussiebox.circuit_core.pal.PALHelper;
import org.aussiebox.circuit_core.pal.animation.PALStackAnimation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PALClientHelper {
    /// Sets the animation of the given {@link PALControllerHandler PALControllerHandler} for a specific player on the client.<br>
    /// To set the animation for the given player on multiple clients, send a {@link org.aussiebox.circuit_core.network.SetAnimationS2CPayload SetAnimationS2CPayload} to each client.
    ///
    /// @return Whether the animation was successfully set
    /// @see org.aussiebox.circuit_core.network.SetAnimationS2CPayload SetAnimationS2CPayload
    /// @see org.aussiebox.circuit_core.pal.PALAnimation PALAnimation
    public static boolean setAnimation(AbstractClientPlayerEntity player, Identifier controller, Identifier animation) {
        if (animation == null) animation = CircuitCoreConstants.NULL_ANIMATION;
        PALControllerHandler handler = getHandler(player, controller);
        if (handler == null) return false;
        handler.animation = animation;
        setHandler(handler);
        return true;
    }

    /// Sets the {@link PALStackAnimation StackAnimation} of the given {@link PALControllerHandler PALControllerHandler} for a specific player on the client.<br>
    /// To set the animation for the given player on multiple clients, send a {@link org.aussiebox.circuit_core.network.SetStackAnimationS2CPayload SetStackAnimationS2CPayload} to each client.
    ///
    /// @return Whether the animation was successfully set
    /// @see org.aussiebox.circuit_core.network.SetStackAnimationS2CPayload SetStackAnimationS2CPayload
    /// @see PALStackAnimation PALStackAnimation
    public static boolean setStackAnimation(AbstractClientPlayerEntity player, Identifier controller, Identifier animation, @Nullable ItemStack stack, @Nullable Hand hand) {
        if (animation == null) animation = CircuitCoreConstants.NULL_ANIMATION;
        PALControllerHandler handler = getHandler(player, controller);
        if (handler == null) return false;
        handler.animation = animation;
        handler.stack = stack;
        handler.activeHand = hand;
        setHandler(handler);
        return true;
    }

    /// Sets the {@link ItemStack ItemStack} of the given {@link PALControllerHandler PALControllerHandler} for a specific player on the client.
    /// @return Whether the {@link ItemStack ItemStack} was successfully set
    public static boolean setStack(AbstractClientPlayerEntity player, Identifier controller, @Nullable ItemStack stack) {
        PALControllerHandler handler = getHandler(player, controller);
        if (handler == null) return false;
        handler.stack = stack;
        setHandler(handler);
        return true;
    }

    /// Sets the active {@link net.minecraft.util.Hand Hand} of the given {@link PALControllerHandler PALControllerHandler} for a specific player on the client.
    /// @return Whether the {@link net.minecraft.util.Hand Hand} was successfully set
    public static boolean setActiveHand(AbstractClientPlayerEntity player, Identifier controller, @Nullable Hand hand) {
        PALControllerHandler handler = getHandler(player, controller);
        if (handler == null) return false;
        handler.activeHand = hand;
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
    /// You shouldn't need to call this, use {@link PALClientHelper#setAnimation(AbstractClientPlayerEntity, Identifier, Identifier) setAnimation()} to set animations instead.
    public static void setHandler(PALControllerHandler handler) {
        Object2ObjectOpenHashMap<UUID, PALControllerHandler> handlers = CircuitCoreClient.handlerRegistry.getOrDefault(handler.controllerId, new Object2ObjectOpenHashMap<>());
        handlers.put(handler.player.getUuid(), handler);
        CircuitCoreClient.handlerRegistry.put(handler.controllerId, handlers);
    }

    public static List<PALControllerHandler> getAllHandlers(AbstractClientPlayerEntity player) {
        List<PALControllerHandler> returnedHandlers = new ArrayList<>();

        for (Identifier controller : CircuitCoreClient.handlerRegistry.keySet()) {
            Object2ObjectOpenHashMap<UUID, PALControllerHandler> handlers = CircuitCoreClient.handlerRegistry.get(controller);
            PALControllerHandler handler = handlers.getOrDefault(player.getUuid(), null);
            if (handler == null) {
                CircuitCore.LOGGER.error("PAL Handler for player {} was not found", player.getNameForScoreboard());
                continue;
            }
            returnedHandlers.add(handler);
        }

        return returnedHandlers;
    }

    public static Object2ObjectOpenHashMap<Identifier, PALStackAnimation> getStackAnimationsOnPlayer(AbstractClientPlayerEntity player) {
        Object2ObjectOpenHashMap<Identifier, PALStackAnimation> returning = new Object2ObjectOpenHashMap<>();
        List<PALControllerHandler> handlers = PALClientHelper.getAllHandlers(player);
        handlers.forEach(handler -> {
            PALStackAnimation stackAnimation = PALHelper.getAnimation(PALStackAnimation.class, handler.controllerId, handler.animation);
            if (stackAnimation != null) returning.put(stackAnimation.id, stackAnimation);
        });
        return returning;
    }

    public static boolean shouldBeLocked(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (MinecraftClient.getInstance().player == null) return false;
        List<PALControllerHandler> handlers = PALClientHelper.getAllHandlers(MinecraftClient.getInstance().player);
        Object2ObjectOpenHashMap<Identifier, PALStackAnimation> stackAnimations = PALClientHelper.getStackAnimationsOnPlayer(MinecraftClient.getInstance().player);

        for (PALControllerHandler handler : handlers) {
            PALStackAnimation animation = null;
            for (PALStackAnimation anim : stackAnimations.values()) {
                if (anim.leftHandedId.equals(handler.animation) || anim.rightHandedId.equals(handler.animation)) {
                    animation = anim;
                    break;
                }
            }
            if (animation == null) continue;
            PALStackAnimation.Behavior behavior = animation.behavior.get();

            if (handler.stack != null) {
                if (handler.activeHand == Hand.MAIN_HAND) {
                    if (ItemStack.areItemsAndComponentsEqual(stack, handler.stack) && ItemStack.areItemsAndComponentsEqual(MinecraftClient.getInstance().player.getMainHandStack(), stack) && behavior.lockSlotWithStack) return true;
                    if (ItemStack.areItemsAndComponentsEqual(MinecraftClient.getInstance().player.getOffHandStack(), stack) && behavior.lockOtherHand) return true;
                }
                if (handler.activeHand == Hand.OFF_HAND) {
                    if (ItemStack.areItemsAndComponentsEqual(stack, handler.stack) && ItemStack.areItemsAndComponentsEqual(MinecraftClient.getInstance().player.getOffHandStack(), stack) && behavior.lockSlotWithStack) return true;
                    if (ItemStack.areItemsAndComponentsEqual(MinecraftClient.getInstance().player.getMainHandStack(), stack) && behavior.lockOtherHand) return true;
                }
            }
        }

        return false;
    }
}
