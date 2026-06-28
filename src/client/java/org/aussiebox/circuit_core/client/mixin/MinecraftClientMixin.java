package org.aussiebox.circuit_core.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @ModifyExpressionValue(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 2))
    private boolean circuitCore$cancelHotbarKeys(boolean original) {
        if (MinecraftClient.getInstance().player == null) return original;
        if (PALClientHelper.shouldBeLocked(MinecraftClient.getInstance().player.getMainHandStack())) return false;
        return original;
    }

    @ModifyExpressionValue(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 6))
    private boolean circuitCore$cancelSwapHandsKey(boolean original) {
        if (MinecraftClient.getInstance().player == null) return original;
        if (PALClientHelper.shouldBeLocked(MinecraftClient.getInstance().player.getMainHandStack())) return false;
        if (PALClientHelper.shouldBeLocked(MinecraftClient.getInstance().player.getOffHandStack())) return false;
        return original;
    }
}
