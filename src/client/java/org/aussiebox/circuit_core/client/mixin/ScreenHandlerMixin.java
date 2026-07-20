package org.aussiebox.circuit_core.client.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.client.helper.PlayerExclusiveItemClientHelper;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.aussiebox.circuit_core.util.ExclusiveItemHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Shadow
    @Final
    public DefaultedList<Slot> slots;

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void circuitCore$cancelSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (actionType == SlotActionType.THROW) return;

        if (slotIndex < 0) return;
        Slot slot = slots.get(slotIndex);
        if (PALClientHelper.shouldBeLocked(slot.getStack(), slotIndex)) ci.cancel();
        else if (!PlayerExclusiveItemClientHelper.playerCanGet(slot.getStack().getItem())) ci.cancel();
    }
}
