package org.aussiebox.circuit_core.pal;

import net.minecraft.util.Identifier;

public class PALHelper {
    public static <A extends PALAnimation> A getAnimation(Class<A> expectedClass, Identifier controller, Identifier animation) {
        PALController palController = ControllerRegistry.getController(controller);
        if (palController == null) return null;

        PALAnimation palAnimation = palController.getAnimation(animation);
        if (expectedClass.isInstance(palAnimation)) return expectedClass.cast(palAnimation);
        else return null;
    }
}
