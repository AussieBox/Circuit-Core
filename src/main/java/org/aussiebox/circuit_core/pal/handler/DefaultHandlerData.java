package org.aussiebox.circuit_core.pal.handler;

import org.aussiebox.circuit_core.pal.animation.AnimationData;
import org.aussiebox.circuit_core.pal.PALAnimation;
import org.jetbrains.annotations.Nullable;

public class DefaultHandlerData implements HandlerData {
    @Nullable private PALAnimation<AnimationData> animation;

    @Override
    public @Nullable PALAnimation<AnimationData> getAnimation() {
        return animation;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setAnimation(@Nullable PALAnimation<? extends AnimationData> animation) {
        if (animation == null) {
            this.animation = null;
            return;
        }
        if (animation.data instanceof AnimationData) this.animation = (PALAnimation<AnimationData>) animation;
    }

    @Override
    public Class<? extends AnimationData> getAnimationDataClass() {
        return AnimationData.class;
    }
}
