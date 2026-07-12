package org.aussiebox.circuit_core.client.mixin;

import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.RawAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AnimationController.class)
public interface AnimationControllerAccessor {
    @Invoker("setAnimation")
    void circuitCore$setAnimation(RawAnimation rawAnimation, float startAnimFrom);

    @Invoker("setAnimation")
    void circuitCore$setAnimation(RawAnimation rawAnimation);
}
