package org.aussiebox.circuit_core.client.pal;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

public class PALEvents {
    private static final Object2ObjectOpenHashMap<Identifier, Function<AbstractClientPlayerEntity, Boolean>> onTickFunctions = new Object2ObjectOpenHashMap<>();

    /// Sets the function that runs every tick for the given animation.<br>
    /// The boolean returned by the function is whether the animation should continue.
    public static void setOnTick(Identifier animationId, Function<AbstractClientPlayerEntity, Boolean> onTick) {
        onTickFunctions.put(animationId, onTick);
    }

    public static Function<AbstractClientPlayerEntity, Boolean> getOnTick(Identifier animationId) {
        return onTickFunctions.getOrDefault(animationId, null);
    }
}
