package org.aussiebox.circuit_core.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? 1.21.1
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
//? >= 1.21.8
//import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    //? 1.21.1 {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;isEnabled()Z"))
    private boolean circuitCore$renderSlotWhenDisabled(boolean original) {
        HandledScreen<?> screen = (HandledScreen<?>)(Object) this;
        if (screen instanceof AbstractInventoryScreen<?>) return true;
        return original;
    }
    //? }

    //? >=1.21.8 {
    /*@ModifyExpressionValue(method = "drawSlots", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;isEnabled()Z"))
    private boolean circuitCore$renderSlotWhenDisabled(boolean original) {
        HandledScreen<?> screen = (HandledScreen<?>)(Object) this;
        if (screen instanceof RecipeBookScreen<?>) return true;
        return original;
    }
    *///? }

    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelClickWhenSlotDisabled(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (slot == null) return;
        if (PALClientHelper.shouldBeLocked(slot.getStack())) ci.cancel();
    }
}
