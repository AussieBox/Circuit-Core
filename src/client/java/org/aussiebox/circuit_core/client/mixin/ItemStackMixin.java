package org.aussiebox.circuit_core.client.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
//? 1.21.1
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.aussiebox.circuit_core.client.helper.PlayerExclusiveItemClientHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract Item getItem();

    //? 1.21.1 {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelUseItem(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!PlayerExclusiveItemClientHelper.playerCanGet(getItem())) {
            cir.setReturnValue(TypedActionResult.fail(user.getStackInHand(hand)));
            cir.cancel();
        }
    }
    //? }
    //? >=1.21.8 {
    /*@Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelUseItem(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!PlayerExclusiveItemClientHelper.playerCanGet(getItem())) {
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
        }
    }
    *///? }

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!PlayerExclusiveItemClientHelper.playerCanGet(getItem())) {
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
        }
    }

    @Inject(method = "useOnEntity", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelUseOnEntity(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!PlayerExclusiveItemClientHelper.playerCanGet(getItem())) {
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
        }
    }

    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelOnClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (!PlayerExclusiveItemClientHelper.playerCanGet(getItem())) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "onStackClicked", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelOnStackClicked(Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (!PlayerExclusiveItemClientHelper.playerCanGet(getItem())) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
