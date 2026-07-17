package org.aussiebox.circuit_core;

import com.zigythebird.playeranimcore.animation.Animation;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.command.MainCommand;
import org.aussiebox.circuit_core.command.PlayerExclusiveItemsCommand;
import org.aussiebox.circuit_core.helper.PlayerExclusiveItemHelper;
import org.aussiebox.circuit_core.helper.item.ItemRegistry;
import org.aussiebox.circuit_core.helper.item.ItemSupplier;
import org.aussiebox.circuit_core.helper.itemgroup.ItemGroupSupplier;
import org.aussiebox.circuit_core.network.SetAnimationS2CPayload;
import org.aussiebox.circuit_core.network.SetStackAnimationS2CPayload;
import org.aussiebox.circuit_core.network.UpdateExclusiveItemsS2CPayload;
import org.aussiebox.circuit_core.pal.ControllerRegistry;
import org.aussiebox.circuit_core.pal.PALAnimation;
import org.aussiebox.circuit_core.pal.PALController;
import org.aussiebox.circuit_core.pal.animation.StackAnimationData;
import org.aussiebox.circuit_core.util.ExclusiveItemHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class CircuitCore implements ModInitializer {
    public static final String MOD_ID = "circuit_core";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static final PALController<StackAnimationData> TEST_CONTROLLER = ControllerRegistry.registerController(
            new PALController<>(id("test"), 1500, StackAnimationData.class,
                    new PALAnimation<>(new StackAnimationData(
                            id("test_anim"),
                            id("test_anim_left"),
                            id("test_anim_right"),
                            Animation.LoopType.HOLD_ON_LAST_FRAME,
                            Items.IRON_SWORD,
                            new StackAnimationData.Behavior(false, true)
                    ))
            )
    );

    @Override
    public void onInitialize() {
        Object2ObjectOpenHashMap<Identifier, ItemGroup> groupIDs = new Object2ObjectOpenHashMap<>();

        FabricLoader.getInstance().getEntrypointContainers("circ_items", ItemSupplier.class)
                .forEach(container -> {
                    ItemSupplier supplier = container.getEntrypoint();
                    supplier.init();
                });
        ItemRegistry.bake();
        FabricLoader.getInstance().getEntrypointContainers("circ_item_groups", ItemGroupSupplier.class)
                .forEach(container -> {
                    ItemGroupSupplier supplier = container.getEntrypoint();
                    groupIDs.put(supplier.getGroupId(), supplier.getGroupBuilder().build());
                });
        for (Map.Entry<Identifier, ItemGroup> entry : groupIDs.entrySet()) Registry.register(Registries.ITEM_GROUP, entry.getKey(), entry.getValue());

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) PlayerExclusiveItemHelper.makeExclusive(Items.IRON_SWORD, UUID.fromString("fdf5edf6-f202-47fe-98f0-68a60d68b0d5"));

        PayloadTypeRegistry.playS2C().register(SetAnimationS2CPayload.ID, SetAnimationS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SetStackAnimationS2CPayload.ID, SetStackAnimationS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateExclusiveItemsS2CPayload.ID, UpdateExclusiveItemsS2CPayload.CODEC);

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            if (FabricLoader.getInstance().isDevelopmentEnvironment()) MainCommand.register(dispatcher, registryAccess);
            PlayerExclusiveItemsCommand.register(dispatcher, registryAccess);
        }));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.getPlayer() instanceof ExclusiveItemHolder holder) holder.circuitCore$syncToClient();
        });
    }
}