package org.aussiebox.circuit_core.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.*;
//? 1.21.1
import net.minecraft.world.World;
import net.minecraft.world.World;
import org.aussiebox.circuit_core.client.helper.PlayerExclusiveItemClientHelper;
import org.aussiebox.circuit_core.helper.PlayerExclusiveItemHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

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

    @ModifyReturnValue(method = "getTooltip", at = @At("RETURN"))
    private List<Text> circuitCore$appendDisabledTooltipLine(List<Text> original, @Local(argsOnly = true) TooltipType type) {
        if (!PlayerExclusiveItemHelper.canBeExclusive(getItem())) return original;
        if (type.isAdvanced()) {
            List<Text> list = new ArrayList<>(original);
            if (!PlayerExclusiveItemClientHelper.playerCanGet(getItem())) list.add(Text.translatable("tooltip.circuit_core.item_disallowed").formatted(Formatting.RED));
            else list.add(Text.translatable("tooltip.circuit_core.item_allowed").formatted(Formatting.DARK_GRAY));
            return list;
        }
        return original;
    }
}
