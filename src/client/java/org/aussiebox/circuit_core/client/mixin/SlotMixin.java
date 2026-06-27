package org.aussiebox.circuit_core.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.aussiebox.circuit_core.client.pal.PALControllerHandler;
import org.aussiebox.circuit_core.pal.animation.PALStackAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow
    public abstract ItemStack getStack();

    @ModifyReturnValue(method = "isEnabled", at = @At("RETURN"))
    private boolean circuitCore$disableSlot(boolean original) {
        if (MinecraftClient.getInstance().player == null) return original;
        List<PALControllerHandler> handlers = PALClientHelper.getAllHandlers(MinecraftClient.getInstance().player);
        Object2ObjectOpenHashMap<Identifier, PALStackAnimation> stackAnimations = PALClientHelper.getStackAnimationsOnPlayer(MinecraftClient.getInstance().player);

        for (PALControllerHandler handler : handlers) {
            if (!stackAnimations.containsKey(handler.animation)) continue;
            PALStackAnimation animation = stackAnimations.get(handler.animation);
            PALStackAnimation.Behavior behavior = animation.behavior.get();

            if (handler.stack != null && ItemStack.areItemsAndComponentsEqual(getStack(), handler.stack)) {
                if ((handler.activeHand == Hand.MAIN_HAND && behavior.lockSlotWithStack) || (handler.activeHand == Hand.OFF_HAND && behavior.lockOtherHand)) {
                    return false;
                }
            }
        }

        return original;
    }

    @Inject(method = "setStackNoCallbacks", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelSetStackNoCallbacks(ItemStack stack, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) ci.cancel();
        List<PALControllerHandler> handlers = PALClientHelper.getAllHandlers(MinecraftClient.getInstance().player);
        Object2ObjectOpenHashMap<Identifier, PALStackAnimation> stackAnimations = PALClientHelper.getStackAnimationsOnPlayer(MinecraftClient.getInstance().player);

        for (PALControllerHandler handler : handlers) {
            if (!stackAnimations.containsKey(handler.animation)) continue;
            PALStackAnimation animation = stackAnimations.get(handler.animation);
            PALStackAnimation.Behavior behavior = animation.behavior.get();

            if (handler.stack != null && ItemStack.areItemsAndComponentsEqual(stack, handler.stack)) {
                if ((handler.activeHand == Hand.MAIN_HAND && behavior.lockSlotWithStack) || (handler.activeHand == Hand.OFF_HAND && behavior.lockOtherHand)) {
                    ci.cancel();
                }
            }
        }
    }
}
