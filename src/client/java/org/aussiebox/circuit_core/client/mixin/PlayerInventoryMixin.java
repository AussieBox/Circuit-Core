package org.aussiebox.circuit_core.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow public abstract int getSwappableHotbarSlot();
    @Shadow public abstract ItemStack getStack(int slot);
    @Shadow public abstract int getEmptySlot();
    @Shadow public int selectedSlot;

    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelRemoveStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (PALClientHelper.shouldBeLocked(getStack(slot), slot)) {
            cir.setReturnValue(ItemStack.EMPTY);
            cir.cancel();
        }
    }

    @Inject(method = "removeStack(I)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelRemoveStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (PALClientHelper.shouldBeLocked(getStack(slot), slot)) {
            cir.setReturnValue(ItemStack.EMPTY);
            cir.cancel();
        }
    }

    @Inject(method = "removeOne", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelRemoveStack(ItemStack stack, CallbackInfo ci) {
        if (PALClientHelper.shouldBeLocked(stack)) ci.cancel();
    }

    @Inject(method = "swapSlotWithHotbar", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelSwapSlot(int slot, CallbackInfo ci) {
        if (PALClientHelper.hotbarLocked()) ci.cancel();
        else if (PALClientHelper.shouldBeLocked(getStack(slot), slot)) ci.cancel();
    }

    @ModifyExpressionValue(method = "getEmptySlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean circuitCore$getEmptySlotIgnoreLocked(boolean original, @Local int i) {
        if (PALClientHelper.shouldBeLocked(i)) return false;
        else return original;
    }

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelInsertStack(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (slot == -1 || PALClientHelper.shouldBeLocked(slot)) slot = getEmptySlot();
        if (slot == -1 || PALClientHelper.shouldBeLocked(getStack(slot), slot)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "setStack", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelSetStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (PALClientHelper.shouldBeLocked(getStack(slot), slot)) ci.cancel();
        else if (PALClientHelper.shouldBeLocked(stack)) ci.cancel();
    }

    //? 1.21.1 {
    /*@Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelScroll(double scrollAmount, CallbackInfo ci) {
        if (PALClientHelper.shouldBeLocked(getStack(selectedSlot), selectedSlot)) ci.cancel();
        else if (PALClientHelper.hotbarLocked()) ci.cancel();
    }
    *///? }

    //? >=1.21.8 {
    @Inject(method = "setSelectedSlot", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelScroll(int slot, CallbackInfo ci) {
        if (PALClientHelper.shouldBeLocked(getStack(selectedSlot), selectedSlot)) ci.cancel();
        else if (PALClientHelper.hotbarLocked()) ci.cancel();
    }
    //? }
}
