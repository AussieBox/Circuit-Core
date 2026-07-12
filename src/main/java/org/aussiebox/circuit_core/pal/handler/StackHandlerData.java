package org.aussiebox.circuit_core.pal.handler;

import net.minecraft.item.ItemStack;
import org.aussiebox.circuit_core.pal.PALAnimation;
import org.aussiebox.circuit_core.pal.animation.AnimationData;
import org.aussiebox.circuit_core.pal.animation.StackAnimationData;
import org.aussiebox.circuit_core.util.Hand;
import org.aussiebox.circuit_core.util.Observable;
import org.jetbrains.annotations.Nullable;

public class StackHandlerData implements HandlerData {
    @Nullable private PALAnimation<StackAnimationData> animation;

    public Observable<ItemStack> stack = Observable.of(null);
    public Observable<Hand> activeHand = Observable.of(Hand.NONE);

    @Override
    public @Nullable PALAnimation<StackAnimationData> getAnimation() {
        return animation;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setAnimation(@Nullable PALAnimation<? extends AnimationData> animation) {
        if (animation == null) {
            this.animation = null;
            return;
        }
        if (animation.data instanceof StackAnimationData) this.animation = (PALAnimation<StackAnimationData>) animation;
    }

    @Override
    public Class<? extends AnimationData> getAnimationDataClass() {
        return StackAnimationData.class;
    }

    public @Nullable net.minecraft.util.Hand getVanillaHand() {
        if (activeHand.get() == Hand.MAIN_HAND) return net.minecraft.util.Hand.MAIN_HAND;
        if (activeHand.get() == Hand.OFF_HAND) return net.minecraft.util.Hand.OFF_HAND;
        return null;
    }
}
