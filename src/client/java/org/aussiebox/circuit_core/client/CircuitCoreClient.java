package org.aussiebox.circuit_core.client;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.animation.PlayerRawAnimationBuilder;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.enums.PlayState;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCoreConstants;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.aussiebox.circuit_core.client.pal.PALControllerHandler;
import org.aussiebox.circuit_core.network.SetAnimationS2CPayload;
import org.aussiebox.circuit_core.network.SetStackAnimationS2CPayload;
import org.aussiebox.circuit_core.pal.ControllerRegistry;
import org.aussiebox.circuit_core.pal.PALAnimation;
import org.aussiebox.circuit_core.pal.PALController;

import java.util.Objects;
import java.util.UUID;

//? >=1.21.10
//import net.fabricmc.fabric.api.resource.v1.ResourceLoader;

public class CircuitCoreClient implements ClientModInitializer {
    /// Registry containing the {@link Identifier Identifier} of every registered {@link PALController PALController}.<br>
    /// Each {@link PALController PALController} also has a collection of {@link PALControllerHandler PALControllerHandlers} mapped to the {@link UUID UUID} of their parent {@link AbstractClientPlayerEntity AbstractClientPlayerEntities}.<br>
    /// This registry is only created when the {@link MinecraftClient MinecraftClient} starts, in order to give other mods time to register their controllers.
    public static final Object2ObjectOpenHashMap<Identifier, Object2ObjectOpenHashMap<UUID, PALControllerHandler>> handlerRegistry = new Object2ObjectOpenHashMap<>();

    @Override
    public void onInitializeClient() {
        //? <1.21.10
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(SecretSillyDetector.INSTANCE);
        //? >=1.21.10
        //ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(SecretSillyDetector.INSTANCE.getFabricId(), SecretSillyDetector.INSTANCE);

//        ConfigRegistry.registerConfig(new Config(CircuitCore.id("test-client")), Config.ConfigType.CLIENT);

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            Object2ObjectOpenHashMap<Identifier, PALController> registry = ControllerRegistry.getControllerRegistry();
            for (Identifier id : registry.keySet()) {
                PALController palController = registry.get(id);

                PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(id, palController.priority, player -> {
                    Object2ObjectOpenHashMap<UUID, PALControllerHandler> handlers = handlerRegistry.getOrDefault(id, new Object2ObjectOpenHashMap<>());
                    handlers.put(player.getUuid(), new PALControllerHandler(id, (AbstractClientPlayerEntity) player));
                    handlerRegistry.put(id, handlers);

                    return new PlayerAnimationController(player, (animationController, animationData, animationSetter) -> {
                        PALController controller = ControllerRegistry.getController(id);
                        if (controller == null) return PlayState.STOP;
                        PALControllerHandler handler = handlerRegistry.get(id).get(player.getUuid());
                        if (handler == null) return PlayState.STOP;

                        if (Objects.equals(handler.animation, CircuitCoreConstants.NO_ANIMATION)) return PlayState.STOP;

                        PALAnimation animation = controller.getAnimation(handler.animation);
                        if (animation != null) return animationSetter.setAnimation(PlayerRawAnimationBuilder.begin().then(animation.id, animation.loopType.get()).build());

                        return PlayState.STOP;
                    });
                });
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(SetAnimationS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                if (world != null) {
                    PlayerEntity targetPlayer = world.getPlayerByUuid(payload.playerUUID());
                    if (targetPlayer instanceof ClientPlayerEntity target) PALClientHelper.setAnimation(target, payload.controller(), payload.animation());
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SetStackAnimationS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                if (world != null) {
                    PlayerEntity targetPlayer = world.getPlayerByUuid(payload.playerUUID());
                    if (targetPlayer instanceof ClientPlayerEntity target) {
                        PALClientHelper.setStack(target, payload.controller(), payload.stack());
                        PALClientHelper.setActiveHand(target, payload.controller(), Hand.valueOf(payload.hand()));
                        PALClientHelper.setAnimation(target, payload.controller(), payload.animation());
                    }
                }
            });
        });
    }
}
