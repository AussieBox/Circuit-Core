package org.aussiebox.circuit_core.pal.handler;

import org.aussiebox.circuit_core.pal.animation.AnimationData;
import org.aussiebox.circuit_core.pal.PALAnimation;

public interface HandlerData {
    PALAnimation<? extends AnimationData> getAnimation();
    void setAnimation(PALAnimation<? extends AnimationData> animation);
    Class<? extends AnimationData> getAnimationDataClass();
}
