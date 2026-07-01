package org.aussiebox.circuit_core;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.command.MainCommand;
import org.aussiebox.circuit_core.helper.item.ItemRegistry;
import org.aussiebox.circuit_core.helper.itemgroup.ItemGroupSupplier;
import org.aussiebox.circuit_core.network.SetAnimationS2CPayload;
import org.aussiebox.circuit_core.network.SetStackAnimationS2CPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CircuitCore implements ModInitializer {
    public static final String MOD_ID = "circuit_core";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

//    public static final PALController TEST_CONTROLLER = ControllerRegistry.registerController(new PALController(id("test"), 1500, new PALStackAnimation(id("test_anim_left"), id("test_anim_right"), Animation.LoopType.HOLD_ON_LAST_FRAME, Items.IRON_SWORD, new PALStackAnimation.Behavior(false, true))));

    @Override
    public void onInitialize() {
        Object2ObjectOpenHashMap<Identifier, ItemGroup> groupIDs = new Object2ObjectOpenHashMap<>();

        ItemRegistry.bake();
        FabricLoader.getInstance().getEntrypointContainers("circ_item_groups", ItemGroupSupplier.class)
                .forEach(container -> {
                    ItemGroupSupplier supplier = container.getEntrypoint();
                    groupIDs.put(supplier.getGroupId(), supplier.getGroupBuilder().build());
                });
        for (Map.Entry<Identifier, ItemGroup> entry : groupIDs.entrySet()) Registry.register(Registries.ITEM_GROUP, entry.getKey(), entry.getValue());

        PayloadTypeRegistry.playS2C().register(SetAnimationS2CPayload.ID, SetAnimationS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SetStackAnimationS2CPayload.ID, SetStackAnimationS2CPayload.CODEC);

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            if (FabricLoader.getInstance().isDevelopmentEnvironment()) MainCommand.register(dispatcher, registryAccess);
        }));
    }
}