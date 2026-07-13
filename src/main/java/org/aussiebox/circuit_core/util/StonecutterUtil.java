package org.aussiebox.circuit_core.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class StonecutterUtil {

    /// Used instead of {@link ServerPlayerEntity#getServer() ServerPlayerEntity.getServer()} for Stonecutter support.
    public static MinecraftServer getServer(ServerPlayerEntity player) {
        //? 1.21.1
        //return player.getServer();
        //? 1.21.8
        //return player.getWorld().getServer();
        //? 1.21.10 || 1.21.11
        return player.getEntityWorld().getServer();
    }

}
