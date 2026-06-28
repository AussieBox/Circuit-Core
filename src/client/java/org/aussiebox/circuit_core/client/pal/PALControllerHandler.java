package org.aussiebox.circuit_core.client.pal;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCoreConstants;
import org.jetbrains.annotations.Nullable;

/// Handles the animation playing in a {@link org.aussiebox.circuit_core.pal.PALController PALController} for a specific player.<br>
/// To configure animations (e.g. loop mode), modify the {@link org.aussiebox.circuit_core.pal.PALAnimation PALAnimation}.
/// @see org.aussiebox.circuit_core.pal.PALController
/// @see org.aussiebox.circuit_core.pal.PALAnimation
public class PALControllerHandler {
    public final Identifier controllerId;
    public final AbstractClientPlayerEntity player;
    public Identifier animation = CircuitCoreConstants.NULL_ANIMATION;
    @Nullable public ItemStack stack = null;
    @Nullable public Hand activeHand = null;

    public PALControllerHandler(Identifier id, AbstractClientPlayerEntity player) {
        this.controllerId = id;
        this.player = player;
    }
}
