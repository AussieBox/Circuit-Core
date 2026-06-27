package org.aussiebox.circuit_core.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.aussiebox.circuit_core.client.pal.PALControllerHandler;
import org.aussiebox.circuit_core.pal.animation.PALStackAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @ModifyExpressionValue(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 2))
    private boolean circuitCore$cancelHotbarKeys(boolean original) {
        if (MinecraftClient.getInstance().player == null) return original;
        if (shouldBeLocked(MinecraftClient.getInstance().player.getMainHandStack())) return false;
        return original;
    }

    @ModifyExpressionValue(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 6))
    private boolean circuitCore$cancelSwapHandsKey(boolean original) {
        if (MinecraftClient.getInstance().player == null) return original;
        if (shouldBeLocked(MinecraftClient.getInstance().player.getMainHandStack())) return false;
        if (shouldBeLocked(MinecraftClient.getInstance().player.getOffHandStack())) return false;
        return original;
    }

    @Unique private boolean shouldBeLocked(ItemStack stack) {
        if (MinecraftClient.getInstance().player == null) return false;
        List<PALControllerHandler> handlers = PALClientHelper.getAllHandlers(MinecraftClient.getInstance().player);
        Object2ObjectOpenHashMap<Identifier, PALStackAnimation> stackAnimations = PALClientHelper.getStackAnimationsOnPlayer(MinecraftClient.getInstance().player);

        for (PALControllerHandler handler : handlers) {
            if (!stackAnimations.containsKey(handler.animation)) continue;
            PALStackAnimation animation = stackAnimations.get(handler.animation);
            PALStackAnimation.Behavior behavior = animation.behavior.get();

            if (handler.stack != null && ItemStack.areItemsAndComponentsEqual(stack, handler.stack)) {
                if ((handler.activeHand == Hand.MAIN_HAND && behavior.lockSlotWithStack) || (handler.activeHand == Hand.OFF_HAND && behavior.lockOtherHand)) {
                    return true;
                }
            }
        }

        return false;
    }
}
