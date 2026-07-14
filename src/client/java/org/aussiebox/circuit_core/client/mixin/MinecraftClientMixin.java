package org.aussiebox.circuit_core.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    //? 1.21.1 {
    @ModifyExpressionValue(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 2))
    private boolean circuitCore$cancelHotbarKeys(boolean original, @Local int i) {
        if (MinecraftClient.getInstance().player == null) return original;
        else if (PALClientHelper.hotbarLocked()) return false;
        return original;
    }
    //? }

    @ModifyExpressionValue(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 6))
    private boolean circuitCore$cancelSwapHandsKey(boolean original) {
        if (MinecraftClient.getInstance().player == null) return original;
        if (PALClientHelper.shouldBeLocked(MinecraftClient.getInstance().player.getMainHandStack())) return false;
        if (PALClientHelper.shouldBeLocked(MinecraftClient.getInstance().player.getOffHandStack())) return false;
        return original;
    }
}
