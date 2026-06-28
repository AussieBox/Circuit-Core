package org.aussiebox.circuit_core;

import com.zigythebird.playeranimcore.animation.Animation;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.command.MainCommand;
import org.aussiebox.circuit_core.helper.ItemGroupHelper;
import org.aussiebox.circuit_core.network.SetAnimationS2CPayload;
import org.aussiebox.circuit_core.network.SetStackAnimationS2CPayload;
import org.aussiebox.circuit_core.pal.ControllerRegistry;
import org.aussiebox.circuit_core.pal.PALController;
import org.aussiebox.circuit_core.pal.animation.PALStackAnimation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitCore implements ModInitializer {

    public static final String MOD_ID = "circuit_core";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static final PALController TEST_CONTROLLER = ControllerRegistry.registerController(new PALController(id("test"), 1500, new PALStackAnimation(id("test_anim_left"), id("test_anim_right"), Animation.LoopType.HOLD_ON_LAST_FRAME, Items.IRON_SWORD, new PALStackAnimation.Behavior(false, true))));

    @Override
    public void onInitialize() {
        ItemGroupHelper.init();

//        ConfigRegistry.registerConfig(new Config(id("test-server")), Config.ConfigType.SERVER);
//
//        ItemGroupHelper.registerGroup(FabricItemGroup.builder().displayName(Text.literal("test")).build(), id(MOD_ID));
//        ItemHelper.registerItem(
//                id("test"),
//                Item::new,
//                new Item.Settings(),
//                null
//        );

//        TEST_CONTROLLER.getAnimation(id("test_anim")).loopType.set(Animation.LoopType.HOLD_ON_LAST_FRAME);

        PayloadTypeRegistry.playS2C().register(SetAnimationS2CPayload.ID, SetAnimationS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SetStackAnimationS2CPayload.ID, SetStackAnimationS2CPayload.CODEC);

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            if (FabricLoader.getInstance().isDevelopmentEnvironment()) MainCommand.register(dispatcher, registryAccess);
        }));
    }
}