package org.aussiebox.circuit_core.pal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.util.Identifier;

public class ControllerRegistry {
    @Getter protected static final Object2ObjectOpenHashMap<Identifier, PALController> controllerRegistry = new Object2ObjectOpenHashMap<>();

    public static PALController registerController(PALController controller) {
        controllerRegistry.put(controller.id, controller);
        controller.observe(ControllerRegistry::updateController);
        return controller;
    }

    public static void registerControllers(PALController... controllers) {
        for (PALController controller : controllers) controllerRegistry.put(controller.id, controller);
    }

    public static void updateController(PALController controller) {
        if (controllerRegistry.containsKey(controller.id)) controllerRegistry.put(controller.id, controller);
    }

    public static PALController getController(Identifier id) {
        return controllerRegistry.getOrDefault(id, null);
    }
}
