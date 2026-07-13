package org.aussiebox.circuit_core.client.pal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.pal.handler.HandlerData;

public interface PALHandlerHolder {
    void circuitCore$setHandler(Identifier id, PALControllerHandler<? extends HandlerData> handler);
    void circuitCore$setHandlers(Object2ObjectOpenHashMap<Identifier, PALControllerHandler<? extends HandlerData>> handlers);
    Object2ObjectOpenHashMap<Identifier, PALControllerHandler<? extends HandlerData>> circuitCore$getHandlers();
}
