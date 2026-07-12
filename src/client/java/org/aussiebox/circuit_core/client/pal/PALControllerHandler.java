package org.aussiebox.circuit_core.client.pal;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.animation.PlayerRawAnimationBuilder;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.client.mixin.AnimationControllerAccessor;
import org.aussiebox.circuit_core.pal.ControllerRegistry;
import org.aussiebox.circuit_core.pal.PALController;
import org.aussiebox.circuit_core.pal.animation.AnimationData;
import org.aussiebox.circuit_core.pal.animation.StackAnimationData;
import org.aussiebox.circuit_core.pal.handler.DefaultHandlerData;
import org.aussiebox.circuit_core.pal.handler.HandlerData;
import org.aussiebox.circuit_core.pal.PALAnimation;
import org.aussiebox.circuit_core.pal.handler.StackHandlerData;
import org.aussiebox.circuit_core.util.Hand;

/// Handles the animation playing in a {@link PALController PALController} for a specific player.<br>
/// To configure animations (e.g. loop mode), modify the {@link PALAnimation PALAnimation}.
/// @see PALController
/// @see PALAnimation
public class PALControllerHandler<D extends HandlerData> {
    public final Identifier controllerId;
    public final AbstractClientPlayerEntity player;
    public final D data;

    public PALControllerHandler(Identifier id, AbstractClientPlayerEntity player, D data) {
        this.controllerId = id;
        this.player = player;
        this.data = data;
    }

    public void setAnimation(PALAnimation<? extends AnimationData> animation) {
        if (data.getAnimationDataClass().isInstance(animation.data)) data.setAnimation(animation);
    }

    public void tick() {
        PALController<? extends AnimationData> controller = ControllerRegistry.getController(controllerId);
        PlayerAnimationController palController = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(player, controllerId);
        if (controller == null || palController == null) return;

        switch (data) {
            case DefaultHandlerData handlerData -> {
                PALAnimation<AnimationData> animation = handlerData.getAnimation();
                if (animation == null) {
                    palController.forceAnimationReset();
                    palController.stop();

                    handlerData.setAnimation(null); // Just to make sure
                } else ((AnimationControllerAccessor) palController).circuitCore$setAnimation(PlayerRawAnimationBuilder.begin().then(animation.data.id, animation.data.loopType.get()).build());
            }
            case StackHandlerData handlerData -> {
                PALAnimation<StackAnimationData> animation = handlerData.getAnimation();
                if (animation == null) {
                    palController.forceAnimationReset();
                    palController.stop();

                    handlerData.setAnimation(null); // Just to make sure (now comes with a free comment included!)
                    handlerData.stack.set(null);
                    handlerData.activeHand.set(Hand.NONE);
                    return;
                }

                net.minecraft.util.Hand vanillaHand = handlerData.getVanillaHand();
                if (handlerData.activeHand.get() == Hand.AUTO) {
                    if (player.getStackInHand(net.minecraft.util.Hand.MAIN_HAND).isOf(animation.data.expectedItem.get())) {
                        handlerData.activeHand.set(Hand.MAIN_HAND);
                        vanillaHand = net.minecraft.util.Hand.MAIN_HAND;
                    }
                    else if (player.getStackInHand(net.minecraft.util.Hand.OFF_HAND).isOf(animation.data.expectedItem.get())) {
                        handlerData.activeHand.set(Hand.OFF_HAND);
                        vanillaHand = net.minecraft.util.Hand.OFF_HAND;
                    }
                    else {
                        palController.forceAnimationReset();
                        palController.stop();

                        handlerData.setAnimation(null);
                        handlerData.stack.set(null);
                        handlerData.activeHand.set(Hand.NONE);
                        return;
                    }
                }
                if (handlerData.stack.get() == null || vanillaHand == null || !player.getStackInHand(vanillaHand).isOf(animation.data.expectedItem.get())) {
                    palController.forceAnimationReset();
                    palController.stop();

                    handlerData.setAnimation(null);
                    handlerData.stack.set(null);
                    handlerData.activeHand.set(Hand.NONE);
                    return;
                }

                if (player.getMainArm() == Arm.LEFT) ((AnimationControllerAccessor) palController).circuitCore$setAnimation(PlayerRawAnimationBuilder.begin().then(vanillaHand == net.minecraft.util.Hand.MAIN_HAND ? animation.data.leftHandedId : animation.data.rightHandedId, animation.data.loopType.get()).build());
                else if (player.getMainArm() == Arm.RIGHT) ((AnimationControllerAccessor) palController).circuitCore$setAnimation(PlayerRawAnimationBuilder.begin().then(vanillaHand == net.minecraft.util.Hand.MAIN_HAND ? animation.data.rightHandedId : animation.data.leftHandedId, animation.data.loopType.get()).build());
            }
            case null, default -> {}
        }
    }
}
