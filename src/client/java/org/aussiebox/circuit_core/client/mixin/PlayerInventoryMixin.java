package org.aussiebox.circuit_core.client.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.aussiebox.circuit_core.client.pal.PALControllerHandler;
import org.aussiebox.circuit_core.pal.animation.PALStackAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow public abstract int getSwappableHotbarSlot();
    @Shadow public abstract ItemStack getStack(int slot);
    @Shadow public abstract int getEmptySlot();
    @Shadow public int selectedSlot;

    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelRemoveStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (shouldBeLocked(getStack(slot))) {
            cir.setReturnValue(ItemStack.EMPTY);
            cir.cancel();
        }
    }

    @Inject(method = "removeStack(I)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelRemoveStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (shouldBeLocked(getStack(slot))) {
            cir.setReturnValue(ItemStack.EMPTY);
            cir.cancel();
        }
    }

    @Inject(method = "removeOne", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelRemoveStack(ItemStack stack, CallbackInfo ci) {
        if (shouldBeLocked(stack)) ci.cancel();
    }

    @Inject(method = "swapSlotWithHotbar", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelSwapSlot(int slot, CallbackInfo ci) {
        Thread.dumpStack();
        if (shouldBeLocked(getStack(getSwappableHotbarSlot()))) ci.cancel();
    }

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelInsertStack(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (slot == -1) slot = getEmptySlot();
        if (shouldBeLocked(getStack(slot))) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "setStack", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelSetStack(int slot, ItemStack stack, CallbackInfo ci) {
        Thread.dumpStack();
        if (shouldBeLocked(getStack(slot))) ci.cancel();
    }

    //? 1.21.1 {
    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelScroll(double scrollAmount, CallbackInfo ci) {
        if (shouldBeLocked(getStack(selectedSlot))) ci.cancel();
    }
    //? }

    //? >=1.21.8 {
    /*@Inject(method = "setSelectedSlot", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelScroll(int slot, CallbackInfo ci) {
        if (shouldBeLocked(getStack(selectedSlot))) ci.cancel();
    }
    *///? }

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
