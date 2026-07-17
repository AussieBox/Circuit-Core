package org.aussiebox.circuit_core.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.client.helper.PlayerExclusiveItemClientHelper;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelAttack(CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == null) return;
        ItemStack itemStack = MinecraftClient.getInstance().player.getStackInHand(Hand.MAIN_HAND);
        if (!PlayerExclusiveItemClientHelper.playerCanGet(itemStack.getItem())) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
