package org.aussiebox.circuit_core.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
//? >=1.21.10
//import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.aussiebox.circuit_core.client.helper.PlayerExclusiveItemClientHelper;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.aussiebox.circuit_core.helper.PlayerExclusiveItemHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? 1.21.1
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
//? >= 1.21.8
//import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Shadow
    @Nullable
    protected Slot focusedSlot;

    //? 1.21.1 {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;isEnabled()Z"))
    private boolean circuitCore$renderSlotWhenDisabled(boolean original, @Local Slot slot) {
        HandledScreen<?> screen = (HandledScreen<?>)(Object) this;
        if (screen instanceof AbstractInventoryScreen<?>) return true;
        else if (slot.inventory instanceof PlayerInventory) return true;
        else if (PlayerExclusiveItemClientHelper.playerCanGet(slot.getStack().getItem())) return true;
        else if (PlayerExclusiveItemHelper.canBeExclusive(slot.getStack().getItem())) return true;
        return original;
    }
    //? }

    //? >=1.21.8 {
    /*@ModifyExpressionValue(method = "drawSlots", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;isEnabled()Z"))
    private boolean circuitCore$renderSlotWhenDisabled(boolean original, @Local Slot slot) {
        HandledScreen<?> screen = (HandledScreen<?>)(Object) this;
        if (screen instanceof RecipeBookScreen<?>) return true;
        else if (slot.inventory instanceof PlayerInventory) return true;
        else if (PlayerExclusiveItemClientHelper.playerCanGet(slot.getStack().getItem())) return true;
        else if (PlayerExclusiveItemHelper.canBeExclusive(slot.getStack().getItem())) return true;
        return original;
    }
    *///? }

    @ModifyExpressionValue(method = "getSlotAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;isEnabled()Z"))
    private boolean circuitCore$allowGettingDisabledSlots(boolean original, @Local Slot slot) {
        HandledScreen<?> screen = (HandledScreen<?>)(Object) this;
        //? 1.21.1
        if (screen instanceof AbstractInventoryScreen<?>) return true;
        //? >=1.21.8
        //if (screen instanceof RecipeBookScreen<?>) return true;
        else if (slot.inventory instanceof PlayerInventory) return true;
        else if (PlayerExclusiveItemClientHelper.playerCanGet(slot.getStack().getItem())) return true;
        else if (PlayerExclusiveItemHelper.canBeExclusive(slot.getStack().getItem())) return true;
        return original;
    }

    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelClickWhenSlotDisabled(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (slot == null || actionType == SlotActionType.THROW) return;
        if (PALClientHelper.shouldBeLocked(slot.getStack(), slotId)) ci.cancel();
        else if (!PlayerExclusiveItemClientHelper.playerCanGet(slot.getStack().getItem())) ci.cancel();
    }

    //? <=1.21.8 {
    @Inject(method = "handleHotbarKeyPressed", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelHotbarKeyPressed(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == null) return;

        if (MinecraftClient.getInstance().options.swapHandsKey.matchesKey(keyCode, scanCode) && focusedSlot != null) {
            if (PALClientHelper.shouldBeLocked(focusedSlot.getStack()) || PALClientHelper.shouldBeLocked(MinecraftClient.getInstance().player.getOffHandStack())) {
                cir.setReturnValue(false);
                cir.cancel();
                return;
            }
        }
        for (int i = 0; i < 9; i++) {
            if (MinecraftClient.getInstance().options.hotbarKeys[i].matchesKey(keyCode, scanCode)) {
                if (PALClientHelper.shouldBeLocked(i)) {
                    cir.setReturnValue(false);
                    cir.cancel();
                    return;
                }
            }
        }
    }
    //? }

    //? >=1.21.10 {
    /*@Inject(method = "handleHotbarKeyPressed", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelHotbarKeyPressed(KeyInput keyInput, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == null) return;

        if (MinecraftClient.getInstance().options.swapHandsKey.matchesKey(keyInput) && focusedSlot != null) {
            if (PALClientHelper.shouldBeLocked(focusedSlot.getStack()) || PALClientHelper.shouldBeLocked(MinecraftClient.getInstance().player.getOffHandStack())) {
                cir.setReturnValue(false);
                cir.cancel();
                return;
            }
        }
        for (int i = 0; i < 9; i++) {
            if (MinecraftClient.getInstance().options.hotbarKeys[i].matchesKey(keyInput)) {
                if (PALClientHelper.shouldBeLocked(i)) {
                    cir.setReturnValue(false);
                    cir.cancel();
                    return;
                }
            }
        }
    }
    *///? }
}
