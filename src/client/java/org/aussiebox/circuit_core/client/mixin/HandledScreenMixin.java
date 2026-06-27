package org.aussiebox.circuit_core.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? 1.21.1
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
}
