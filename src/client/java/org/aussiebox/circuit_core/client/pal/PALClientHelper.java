package org.aussiebox.circuit_core.client.pal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.client.CircuitCoreClient;
import org.aussiebox.circuit_core.pal.PALAnimation;
import org.aussiebox.circuit_core.pal.animation.AnimationData;
import org.aussiebox.circuit_core.pal.animation.StackAnimationData;
import org.aussiebox.circuit_core.pal.handler.HandlerData;
import org.aussiebox.circuit_core.pal.handler.StackHandlerData;
import org.aussiebox.circuit_core.util.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PALClientHelper {
    /// Fetches the {@link PALControllerHandler PALControllerHandler} of a specific player for the given controller.
    ///
    /// @return The {@link PALControllerHandler PALControllerHandler} instance, or {@code null}
    public static PALControllerHandler<? extends HandlerData> getHandler(AbstractClientPlayerEntity player, Identifier controller) {
        Object2ObjectOpenHashMap<UUID, PALControllerHandler<? extends HandlerData>> handlers = CircuitCoreClient.handlerRegistry.getOrDefault(controller, null);
        if (handlers == null) {
            CircuitCore.LOGGER.error("PAL Controller {} was not found", controller.toString());
            return null;
        }
        PALControllerHandler<? extends HandlerData> handler = handlers.getOrDefault(player.getUuid(), null);
        if (handler == null) {
            CircuitCore.LOGGER.error("(getHandler) PAL Handler for player {} was not found", player.getNameForScoreboard());
            return null;
        }
        return handler;
    }

    @SuppressWarnings("unchecked")
    public static <D extends HandlerData> PALControllerHandler<D> getHandler(AbstractClientPlayerEntity player, Identifier controller, Class<D> dataClass) {
        Object2ObjectOpenHashMap<UUID, PALControllerHandler<? extends HandlerData>> handlers = CircuitCoreClient.handlerRegistry.getOrDefault(controller, null);
        if (handlers == null) {
            CircuitCore.LOGGER.error("PAL Controller {} was not found", controller.toString());
            return null;
        }
        PALControllerHandler<? extends HandlerData> handler = handlers.getOrDefault(player.getUuid(), null);
        if (handler == null) {
            CircuitCore.LOGGER.error("(getHandler TYPED) PAL Handler for player {} was not found", player.getNameForScoreboard());
            return null;
        }
        if (dataClass.isInstance(handler.data)) return (PALControllerHandler<D>) handler;
        else {
            CircuitCore.LOGGER.error("PAL Handler for player {} was of incorrect data type: Expected {}, was {}", player.getNameForScoreboard(), dataClass.getName(), handler.data.getClass().getName());
            return null;
        }
    }

    public static List<PALControllerHandler<? extends HandlerData>> getAllHandlers(AbstractClientPlayerEntity player) {
        List<PALControllerHandler<? extends HandlerData>> returnedHandlers = new ArrayList<>();

        for (Map.Entry<Identifier, Object2ObjectOpenHashMap<UUID, PALControllerHandler<? extends HandlerData>>> entry : CircuitCoreClient.handlerRegistry.entrySet()) {
            Object2ObjectOpenHashMap<UUID, PALControllerHandler<? extends HandlerData>> handlers = entry.getValue();
            PALControllerHandler<? extends HandlerData> handler = handlers.getOrDefault(player.getUuid(), null);
            if (handler == null) {
                CircuitCore.LOGGER.error("(getAllHandlers) PAL Handler for player {} was not found", player.getNameForScoreboard());
                continue;
            }
            returnedHandlers.add(handler);
        }

        return returnedHandlers;
    }

    @SuppressWarnings("unchecked")
    public static Object2ObjectOpenHashMap<Identifier, PALAnimation<StackAnimationData>> getStackAnimationsOnPlayer(AbstractClientPlayerEntity player) {
        Object2ObjectOpenHashMap<Identifier, PALAnimation<StackAnimationData>> returning = new Object2ObjectOpenHashMap<>();
        List<PALControllerHandler<? extends HandlerData>> handlers = PALClientHelper.getAllHandlers(player);
        handlers.forEach(handler -> {
            PALAnimation<? extends AnimationData> animation = handler.data.getAnimation();
            if (animation != null && animation.data instanceof StackAnimationData) returning.put(animation.data.id, (PALAnimation<StackAnimationData>) animation);
        });
        return returning;
    }

    public static boolean shouldBeLocked(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (MinecraftClient.getInstance().player == null) return false;
        List<PALControllerHandler<? extends HandlerData>> handlers = PALClientHelper.getAllHandlers(MinecraftClient.getInstance().player);
        Object2ObjectOpenHashMap<Identifier, PALAnimation<StackAnimationData>> stackAnimations = PALClientHelper.getStackAnimationsOnPlayer(MinecraftClient.getInstance().player);

        for (PALControllerHandler<? extends HandlerData> handler : handlers) {
            if (!(handler.data instanceof StackHandlerData data)) continue;
            if (data.getAnimation() == null) continue;

            PALAnimation<StackAnimationData> animation = null;
            for (PALAnimation<StackAnimationData> anim : stackAnimations.values()) {
                if (anim.data.matchesId(data.getAnimation().data.id)) {
                    animation = anim;
                    break;
                }
            }
            if (animation == null) continue;
            StackAnimationData.Behavior behavior = animation.data.behavior.get();

            if (data.stack.get() != null) {
                Hand hand = data.activeHand.get();
                if (hand == Hand.AUTO) {
                    if (MinecraftClient.getInstance().player.getStackInHand(net.minecraft.util.Hand.MAIN_HAND).isOf(animation.data.expectedItem.get())) hand = Hand.MAIN_HAND;
                    else if (MinecraftClient.getInstance().player.getStackInHand(net.minecraft.util.Hand.OFF_HAND).isOf(animation.data.expectedItem.get())) hand = Hand.OFF_HAND;
                }
                if (hand == Hand.MAIN_HAND) {
                    if (ItemStack.areItemsAndComponentsEqual(stack, data.stack.get()) && ItemStack.areItemsAndComponentsEqual(MinecraftClient.getInstance().player.getMainHandStack(), stack) && behavior.lockSlotWithStack) return true;
                    if (ItemStack.areItemsAndComponentsEqual(MinecraftClient.getInstance().player.getOffHandStack(), stack) && behavior.lockOtherHand) return true;
                }
                if (hand == Hand.OFF_HAND) {
                    if (ItemStack.areItemsAndComponentsEqual(stack, data.stack.get()) && ItemStack.areItemsAndComponentsEqual(MinecraftClient.getInstance().player.getOffHandStack(), stack) && behavior.lockSlotWithStack) return true;
                    if (ItemStack.areItemsAndComponentsEqual(MinecraftClient.getInstance().player.getMainHandStack(), stack) && behavior.lockOtherHand) return true;
                }
            }
        }

        return false;
    }

    public static boolean shouldBeLocked(int slotIndex) {
        if (MinecraftClient.getInstance().player == null) return false;
        List<PALControllerHandler<? extends HandlerData>> handlers = PALClientHelper.getAllHandlers(MinecraftClient.getInstance().player);
        Object2ObjectOpenHashMap<Identifier, PALAnimation<StackAnimationData>> stackAnimations = PALClientHelper.getStackAnimationsOnPlayer(MinecraftClient.getInstance().player);

        for (PALControllerHandler<? extends HandlerData> handler : handlers) {
            if (!(handler.data instanceof StackHandlerData data)) continue;
            if (data.getAnimation() == null) continue;

            PALAnimation<StackAnimationData> animation = null;
            for (PALAnimation<StackAnimationData> anim : stackAnimations.values()) {
                if (anim.data.matchesId(data.getAnimation().data.id)) {
                    animation = anim;
                    break;
                }
            }
            if (animation == null) continue;
            StackAnimationData.Behavior behavior = animation.data.behavior.get();

            //? 1.21.1
            int hotbarSlot = MinecraftClient.getInstance().player.getInventory().selectedSlot;
            //? >=1.21.8
            //int hotbarSlot = MinecraftClient.getInstance().player.getInventory().getSelectedSlot();
            Hand hand = data.activeHand.get();
            if (hand == Hand.AUTO) {
                if (MinecraftClient.getInstance().player.getStackInHand(net.minecraft.util.Hand.MAIN_HAND).isOf(animation.data.expectedItem.get())) hand = Hand.MAIN_HAND;
                else if (MinecraftClient.getInstance().player.getStackInHand(net.minecraft.util.Hand.OFF_HAND).isOf(animation.data.expectedItem.get())) hand = Hand.OFF_HAND;
            }
            if (hand == Hand.MAIN_HAND) {
                if (slotIndex == hotbarSlot && behavior.lockSlotWithStack) return true;
                if (slotIndex == 40 && behavior.lockOtherHand) return true;
            }
            if (hand == Hand.OFF_HAND) {
                if (slotIndex == 40 && behavior.lockSlotWithStack) return true;
                if (slotIndex == hotbarSlot && behavior.lockOtherHand) return true;
            }
        }

        return false;
    }

    public static boolean shouldBeLocked(ItemStack stack, int slotIndex) {
        if (stack.isEmpty()) return false;
        if (MinecraftClient.getInstance().player == null) return false;
        List<PALControllerHandler<? extends HandlerData>> handlers = PALClientHelper.getAllHandlers(MinecraftClient.getInstance().player);
        Object2ObjectOpenHashMap<Identifier, PALAnimation<StackAnimationData>> stackAnimations = PALClientHelper.getStackAnimationsOnPlayer(MinecraftClient.getInstance().player);

        for (PALControllerHandler<? extends HandlerData> handler : handlers) {
            if (!(handler.data instanceof StackHandlerData data)) continue;
            if (data.getAnimation() == null) continue;

            PALAnimation<StackAnimationData> animation = null;
            for (PALAnimation<StackAnimationData> anim : stackAnimations.values()) {
                if (anim.data.matchesId(data.getAnimation().data.id)) {
                    animation = anim;
                    break;
                }
            }
            if (animation == null) continue;
            StackAnimationData.Behavior behavior = animation.data.behavior.get();

            //? 1.21.1
            int hotbarSlot = MinecraftClient.getInstance().player.getInventory().selectedSlot;
            //? >=1.21.8
            //int hotbarSlot = MinecraftClient.getInstance().player.getInventory().getSelectedSlot();
            if (data.stack.get() != null) {
                Hand hand = data.activeHand.get();
                if (hand == Hand.AUTO) {
                    if (MinecraftClient.getInstance().player.getStackInHand(net.minecraft.util.Hand.MAIN_HAND).isOf(animation.data.expectedItem.get())) hand = Hand.MAIN_HAND;
                    else if (MinecraftClient.getInstance().player.getStackInHand(net.minecraft.util.Hand.OFF_HAND).isOf(animation.data.expectedItem.get())) hand = Hand.OFF_HAND;
                }
                if (hand == Hand.MAIN_HAND) {
                    if (slotIndex == hotbarSlot && ItemStack.areItemsAndComponentsEqual(stack, data.stack.get()) && ItemStack.areItemsAndComponentsEqual(MinecraftClient.getInstance().player.getMainHandStack(), stack) && behavior.lockSlotWithStack) return true;
                    if (slotIndex == 40 && ItemStack.areItemsAndComponentsEqual(MinecraftClient.getInstance().player.getOffHandStack(), stack) && behavior.lockOtherHand) return true;
                }
                if (hand == Hand.OFF_HAND) {
                    if (slotIndex == 40 && ItemStack.areItemsAndComponentsEqual(stack, data.stack.get()) && ItemStack.areItemsAndComponentsEqual(MinecraftClient.getInstance().player.getOffHandStack(), stack) && behavior.lockSlotWithStack) return true;
                    if (slotIndex == hotbarSlot && ItemStack.areItemsAndComponentsEqual(MinecraftClient.getInstance().player.getMainHandStack(), stack) && behavior.lockOtherHand) return true;
                }
            }
        }

        return false;
    }

    public static boolean hotbarLocked() {
        if (MinecraftClient.getInstance().player == null) return false;
        List<PALControllerHandler<? extends HandlerData>> handlers = PALClientHelper.getAllHandlers(MinecraftClient.getInstance().player);
        Object2ObjectOpenHashMap<Identifier, PALAnimation<StackAnimationData>> stackAnimations = PALClientHelper.getStackAnimationsOnPlayer(MinecraftClient.getInstance().player);

        for (PALControllerHandler<? extends HandlerData> handler : handlers) {
            if (!(handler.data instanceof StackHandlerData data)) continue;
            if (data.getAnimation() == null) continue;

            PALAnimation<StackAnimationData> animation = null;
            for (PALAnimation<StackAnimationData> anim : stackAnimations.values()) {
                if (anim.data.matchesId(data.getAnimation().data.id)) {
                    animation = anim;
                    break;
                }
            }
            if (animation == null) continue;
            StackAnimationData.Behavior behavior = animation.data.behavior.get();

            if (data.stack.get() != null) {
                Hand hand = data.activeHand.get();
                if (hand == Hand.AUTO) {
                    if (MinecraftClient.getInstance().player.getStackInHand(net.minecraft.util.Hand.MAIN_HAND).isOf(animation.data.expectedItem.get())) hand = Hand.MAIN_HAND;
                    else if (MinecraftClient.getInstance().player.getStackInHand(net.minecraft.util.Hand.OFF_HAND).isOf(animation.data.expectedItem.get())) hand = Hand.OFF_HAND;
                }
                if (hand == Hand.MAIN_HAND && behavior.lockSlotWithStack) return true;
                if (hand == Hand.OFF_HAND && behavior.lockOtherHand) return true;
            }
        }

        return false;
    }
}
