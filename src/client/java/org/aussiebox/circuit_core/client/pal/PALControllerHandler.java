package org.aussiebox.circuit_core.client.pal;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

/// Handles the animation playing in a {@link org.aussiebox.circuit_core.pal.PALController PALController} for a specific player.<br>
/// To configure animations (e.g. loop mode), modify the {@link org.aussiebox.circuit_core.pal.PALAnimation PALAnimation}.
///
/// @see org.aussiebox.circuit_core.pal.PALController
/// @see org.aussiebox.circuit_core.pal.PALAnimation
public class PALControllerHandler {
    public final Identifier controllerId;
    public final AbstractClientPlayerEntity player;
    public Identifier animation = Identifier.of("null");

    public PALControllerHandler(Identifier id, AbstractClientPlayerEntity player) {
        this.controllerId = id;
        this.player = player;
    }
}
