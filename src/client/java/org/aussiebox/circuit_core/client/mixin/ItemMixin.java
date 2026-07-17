package org.aussiebox.circuit_core.client.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
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

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow
    public abstract Item asItem();

    //? 1.21.1 {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelUseItem(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!PlayerExclusiveItemClientHelper.playerCanGet(asItem())) {
            cir.setReturnValue(TypedActionResult.fail(user.getStackInHand(hand)));
            cir.cancel();
        }
    }
    //? }
    //? >=1.21.8 {
    /*@Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelUseItem(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!PlayerExclusiveItemClientHelper.playerCanGet(asItem())) {
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
        }
    }
    *///? }

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!PlayerExclusiveItemClientHelper.playerCanGet(asItem())) {
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
        }
    }

    @Inject(method = "useOnEntity", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelUseOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!PlayerExclusiveItemClientHelper.playerCanGet(asItem())) {
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
        }
    }
}
