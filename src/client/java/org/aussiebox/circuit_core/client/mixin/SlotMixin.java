package org.aussiebox.circuit_core.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow
    public abstract int getIndex();

    @ModifyReturnValue(method = "isEnabled", at = @At("RETURN"))
    private boolean circuitCore$disableSlot(boolean original) {
        if (PALClientHelper.shouldBeLocked(getIndex())) return false;
        else return original;
    }

    @Inject(method = "setStackNoCallbacks", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelSetStackNoCallbacks(ItemStack stack, CallbackInfo ci) {
        if (PALClientHelper.shouldBeLocked(stack, getIndex())) ci.cancel();
    }
}
