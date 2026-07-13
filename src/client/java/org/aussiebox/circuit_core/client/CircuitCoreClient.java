package org.aussiebox.circuit_core.client;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.animation.PlayerRawAnimationBuilder;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.enums.PlayState;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.client.pal.PALClientHelper;
import org.aussiebox.circuit_core.client.pal.PALControllerHandler;
import org.aussiebox.circuit_core.client.pal.PALHandlerHolder;
import org.aussiebox.circuit_core.pal.animation.StackAnimationData;
import org.aussiebox.circuit_core.pal.handler.DefaultHandlerData;
import org.aussiebox.circuit_core.pal.handler.HandlerData;
import org.aussiebox.circuit_core.network.SetAnimationS2CPayload;
import org.aussiebox.circuit_core.network.SetStackAnimationS2CPayload;
import org.aussiebox.circuit_core.pal.animation.AnimationData;
import org.aussiebox.circuit_core.pal.ControllerRegistry;
import org.aussiebox.circuit_core.pal.PALAnimation;
import org.aussiebox.circuit_core.pal.PALController;
import org.aussiebox.circuit_core.pal.handler.StackHandlerData;
import org.aussiebox.circuit_core.util.Hand;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//? >=1.21.10
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;

public class CircuitCoreClient implements ClientModInitializer {
    /// Registry containing the {@link Identifier Identifier} of every registered {@link PALController PALController}.<br>
    /// Each {@link PALController PALController} also has a collection of {@link PALControllerHandler PALControllerHandlers} mapped to the {@link UUID UUID} of their parent {@link AbstractClientPlayerEntity AbstractClientPlayerEntities}.<br>
    /// This registry is only created when the {@link MinecraftClient MinecraftClient} starts, in order to give other mods time to register their controllers.
    public static final Object2ObjectOpenHashMap<Identifier, Object2ObjectOpenHashMap<UUID, PALControllerHandler<? extends HandlerData>>> handlerRegistry = new Object2ObjectOpenHashMap<>();

    @Override
    public void onInitializeClient() {
        //? <1.21.10
        //ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(SecretSillyDetector.INSTANCE);
        //? >=1.21.10
        ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(SecretSillyDetector.INSTANCE.getFabricId(), SecretSillyDetector.INSTANCE);

//        ConfigRegistry.registerConfig(new Config(CircuitCore.id("test-client")), Config.ConfigType.CLIENT);

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            Object2ObjectOpenHashMap<Identifier, PALController<? extends AnimationData>> registry = ControllerRegistry.getControllerRegistry();
            for (Map.Entry<Identifier, PALController<? extends AnimationData>> entry : registry.entrySet()) {
                Identifier id = entry.getKey();
                PALController<? extends AnimationData> palController = entry.getValue();

                PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(id, palController.priority, player -> {
                    if (player instanceof AbstractClientPlayerEntity playerFr && player instanceof PALHandlerHolder holder)
                        holder.circuitCore$setHandler(id, new PALControllerHandler<>(id, playerFr, palController.createHandlerData()));

                    return new PlayerAnimationController(player, (animationController, animationData, animationSetter) -> PlayState.CONTINUE);
                });
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Object2ObjectOpenHashMap<UUID, PALControllerHandler<? extends HandlerData>> handlers : handlerRegistry.values())
                for (PALControllerHandler<? extends HandlerData> handler : handlers.values()) handler.tick();
        });

        ClientPlayNetworking.registerGlobalReceiver(SetAnimationS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                if (world != null) {
                    PlayerEntity targetPlayer = world.getPlayerByUuid(payload.playerUUID());
                    if (targetPlayer instanceof ClientPlayerEntity target) {
                        PALControllerHandler<? extends HandlerData> handler = PALClientHelper.getHandler(target, payload.controller());
                        PALAnimation<? extends AnimationData> animation = ControllerRegistry.getController(payload.controller()).getAnimation(payload.animation());
                        if (handler != null && animation != null) handler.setAnimation(animation);
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SetStackAnimationS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                if (world != null) {
                    PlayerEntity targetPlayer = world.getPlayerByUuid(payload.playerUUID());
                    if (targetPlayer instanceof ClientPlayerEntity target) {
                        PALControllerHandler<? extends HandlerData> handler = PALClientHelper.getHandler(target, payload.controller());
                        if (handler != null && handler.data instanceof StackHandlerData data) {
                            PALController<StackAnimationData> controller = ControllerRegistry.getController(payload.controller(), StackAnimationData.class);
                            if (controller == null || payload.hand() == Hand.NONE || payload.stack().isEmpty()) {
                                data.stack.set(null);
                                data.activeHand.set(Hand.NONE);
                                data.setAnimation(null);
                                return;
                            }
                            PALAnimation<StackAnimationData> animation = controller.getAnimation(payload.animation());
                            if (animation == null) {
                                data.stack.set(null);
                                data.activeHand.set(Hand.NONE);
                                data.setAnimation(null);
                                return;
                            }
                            data.stack.set(payload.stack().get());
                            data.activeHand.set(payload.hand());
                            data.setAnimation(animation);
                        }
                    }
                }
            });
        });
    }
}
