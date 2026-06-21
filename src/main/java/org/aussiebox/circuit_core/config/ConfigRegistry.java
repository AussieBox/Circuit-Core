package org.aussiebox.circuit_core.config;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;

public class ConfigRegistry {
    public static Object2ObjectOpenHashMap<Identifier, Config> clientConfig = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<Identifier, Config> serverConfig = new Object2ObjectOpenHashMap<>();

    public static void registerConfig(Config config, Config.ConfigType type) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            CircuitCore.LOGGER.info(String.valueOf(config.identifier));
            ConfigRegistry.clientConfig.put(config.identifier, config);
        }
        else ConfigRegistry.serverConfig.put(config.identifier, config);
    }
}
