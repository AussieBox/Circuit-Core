package org.aussiebox.circuit_core.pal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.pal.animation.AnimationData;

public class ControllerRegistry {
    @Getter protected static final Object2ObjectOpenHashMap<Identifier, PALController<? extends AnimationData>> controllerRegistry = new Object2ObjectOpenHashMap<>();

    public static <D extends AnimationData> PALController<D> registerController(PALController<D> controller) {
        controllerRegistry.put(controller.id, controller);
        controller.observe(ControllerRegistry::updateController);
        return controller;
    }

    public static void registerControllers(PALController<?>... controllers) {
        for (PALController<?> controller : controllers) controllerRegistry.put(controller.id, controller);
    }

    public static <D extends AnimationData> void updateController(PALController<D> controller) {
        if (controllerRegistry.containsKey(controller.id)) controllerRegistry.put(controller.id, controller);
    }

    public static PALController<?> getController(Identifier id) {
        return controllerRegistry.getOrDefault(id, null);
    }

    @SuppressWarnings("unchecked") // This is safe, shut up Java!
    public static <D extends AnimationData> PALController<D> getController(Identifier id, Class<D> expectedDataClass) {
        PALController<?> controller = getController(id);
        if (expectedDataClass.isAssignableFrom(controller.dataClass)) return (PALController<D>) controller;
        return null;
    }
}
