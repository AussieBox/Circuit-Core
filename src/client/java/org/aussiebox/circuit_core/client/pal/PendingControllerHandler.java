package org.aussiebox.circuit_core.client.pal;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.animation.PlayerRawAnimationBuilder;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.client.mixin.AnimationControllerAccessor;
import org.aussiebox.circuit_core.pal.ControllerRegistry;
import org.aussiebox.circuit_core.pal.PALAnimation;
import org.aussiebox.circuit_core.pal.PALController;
import org.aussiebox.circuit_core.pal.animation.AnimationData;
import org.aussiebox.circuit_core.pal.animation.StackAnimationData;
import org.aussiebox.circuit_core.pal.handler.DefaultHandlerData;
import org.aussiebox.circuit_core.pal.handler.HandlerData;
import org.aussiebox.circuit_core.pal.handler.StackHandlerData;
import org.aussiebox.circuit_core.util.Hand;

import java.util.function.Function;

public class PendingControllerHandler<D extends HandlerData> {
    public final Identifier controllerId;
    public final D data;

    public PendingControllerHandler(Identifier id, D data) {
        this.controllerId = id;
        this.data = data;
    }

    public PALControllerHandler<D> create(AbstractClientPlayerEntity player) {
        return new PALControllerHandler<>(controllerId, player, data);
    }
}
