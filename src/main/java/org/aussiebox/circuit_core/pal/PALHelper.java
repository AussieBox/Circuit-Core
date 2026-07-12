package org.aussiebox.circuit_core.pal;

import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.pal.animation.AnimationData;

public class PALHelper {
    public static <D extends AnimationData> PALAnimation<D> getAnimation(Class<D> dataClass, Identifier controller, Identifier animation) {
        PALController<D> palController = ControllerRegistry.getController(controller, dataClass);
        if (palController == null) return null;

        return palController.getAnimation(animation);
    }
}
