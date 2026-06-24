package org.aussiebox.circuit_core.client;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;

import java.util.Objects;

public class SecretSillyDetector implements SimpleSynchronousResourceReloadListener {
    public static final SecretSillyDetector INSTANCE = new SecretSillyDetector();

    @Override
    public Identifier getFabricId() {
        return CircuitCore.id("tf2");
    }

    @Override
    public void reload(ResourceManager manager) {
        ResourceFinder finder = new ResourceFinder("textures", "important_file.png");
        boolean pass = false;
        for (Resource resource : finder.findResources(manager).values()) {
            if (resource.getKnownPackInfo().isPresent()) {
                if (Objects.equals(resource.getKnownPackInfo().get().id(), CircuitCore.MOD_ID)) pass = true;
            }
        }
        if (!pass) {
            MinecraftClient.getInstance().scheduleStop();
            throw new IllegalStateException("Something is missing...");
        }
    }
}
