package org.aussiebox.circuit_core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//? 1.21.11
//import net.minecraft.command.DefaultPermissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.CircuitCoreConstants;
import org.aussiebox.circuit_core.network.SetAnimationS2CPayload;
import org.aussiebox.circuit_core.network.SetStackAnimationS2CPayload;
import org.aussiebox.circuit_core.pal.ControllerRegistry;
import org.aussiebox.circuit_core.pal.PALAnimation;
import org.aussiebox.circuit_core.pal.PALController;
import org.aussiebox.circuit_core.pal.PALHelper;
import org.aussiebox.circuit_core.pal.animation.AnimationData;
import org.aussiebox.circuit_core.pal.animation.StackAnimationData;
import org.aussiebox.circuit_core.util.Hand;

import java.util.Objects;
import java.util.Optional;

public class MainCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(
                CommandManager.literal(CircuitCore.MOD_ID)
                        .then(CommandManager.literal("animation")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .then(CommandManager.argument("controller", IdentifierArgumentType.identifier())
                                                .suggests((context, builder) -> {
                                                    ControllerRegistry.getControllerRegistry().forEach((id, controller) -> {
                                                        builder.suggest(id.toString());
                                                    });
                                                    return builder.buildFuture();
                                                })
                                                .then(CommandManager.argument("animation", IdentifierArgumentType.identifier())
                                                        .suggests((context, builder) -> {
                                                            Identifier id = context.getArgument("controller", Identifier.class);
                                                            PALController<AnimationData> controller = ControllerRegistry.getController(id, AnimationData.class);
                                                            if (controller != null) controller.getAnimations().forEach((identifier, animation) -> {
                                                                builder.suggest(identifier.toString());
                                                            });
                                                            return builder.buildFuture();
                                                        })
                                                        .executes(MainCommand::setAnimation)
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("stack_animation")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .then(CommandManager.argument("controller", IdentifierArgumentType.identifier())
                                                .suggests((context, builder) -> {
                                                    ControllerRegistry.getControllerRegistry().forEach((id, controller) -> {
                                                        if (controller.dataClass.isAssignableFrom(StackAnimationData.class)) builder.suggest(id.toString());
                                                    });
                                                    return builder.buildFuture();
                                                })
                                                .then(CommandManager.argument("animation", IdentifierArgumentType.identifier())
                                                        .suggests((context, builder) -> {
                                                            Identifier id = context.getArgument("controller", Identifier.class);
                                                            PALController<StackAnimationData> controller = ControllerRegistry.getController(id, StackAnimationData.class);
                                                            if (controller != null) controller.getAnimations().forEach((identifier, animation) -> {
                                                                builder.suggest(identifier.toString());
                                                            });
                                                            return builder.buildFuture();
                                                        })
                                                        .executes(MainCommand::setStackAnimation)
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("effect")
                                .then(CommandManager.argument("effect", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.STATUS_EFFECT))
                                        .executes((context -> toggleEffect(context, false)))
                                        .then(CommandManager.argument("amplifier",  IntegerArgumentType.integer(0, 255))
                                                .executes((context -> toggleEffect(context, true)))
                                        )
                                )
                        )
        );
    }

    public static int setAnimation(CommandContext<ServerCommandSource> context) {
        if (context.getSource().getPlayer() == null) return 0;

        try {
            PlayerEntity player = context.getArgument("player", EntitySelector.class).getPlayer(context.getSource());
            Identifier controller = context.getArgument("controller", Identifier.class);
            Identifier animation = context.getArgument("animation", Identifier.class);
            if (Objects.equals(animation, Identifier.of("null"))) animation = CircuitCoreConstants.NULL_ANIMATION;

            ServerPlayNetworking.send(context.getSource().getPlayer(), new SetAnimationS2CPayload(player.getUuid(), controller, animation));
        } catch (Exception e) {
            CircuitCore.LOGGER.error("Failed to set animation for player: {}", e.getMessage());
            return 0;
        }

        return 1;
    }

    public static int setStackAnimation(CommandContext<ServerCommandSource> context) {
        if (context.getSource().getPlayer() == null) return 0;

        try {
            PlayerEntity player = context.getArgument("player", EntitySelector.class).getPlayer(context.getSource());
            Identifier controller = context.getArgument("controller", Identifier.class);
            Identifier animation = context.getArgument("animation", Identifier.class);
            Optional<ItemStack> stack = Optional.empty();
            Hand hand = Hand.AUTO;
            if (Objects.equals(animation.getPath(), "null") || Objects.equals(animation.getPath(), "none")) {
                animation = CircuitCoreConstants.NULL_ANIMATION;
                hand = Hand.NONE;
            } else {
                PALAnimation<StackAnimationData> stackAnimation = PALHelper.getAnimation(StackAnimationData.class, controller, animation);
                if (stackAnimation == null) {
                    context.getSource().sendError(Text.translatable("command.circuit_core.main.stack_animation.incorrect_type"));
                    return 1;
                } else {
                    if (player.getMainHandStack().isOf(stackAnimation.data.expectedItem.get())) stack = Optional.of(player.getMainHandStack());
                    else if (player.getOffHandStack().isOf(stackAnimation.data.expectedItem.get())) stack = Optional.of(player.getOffHandStack());
                }
            }

            ServerPlayNetworking.send(context.getSource().getPlayer(), new SetStackAnimationS2CPayload(player.getUuid(), controller, animation, stack, hand));
        } catch (Exception e) {
            CircuitCore.LOGGER.error("Failed to set animation for player: {}", e.getMessage());
            return 0;
        }

        return 1;
    }

    public static int toggleEffect(CommandContext<ServerCommandSource> context, boolean hasAmplifier) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        RegistryEntry<StatusEffect> statusEffect = RegistryEntryReferenceArgumentType.getStatusEffect(context, "effect");

        if (player.hasStatusEffect(statusEffect)) player.removeStatusEffect(statusEffect);
        else {
            if (hasAmplifier) {
                int amplifier = IntegerArgumentType.getInteger(context, "amplifier");
                player.addStatusEffect(new StatusEffectInstance(statusEffect, -1, amplifier, false, false));
            } else player.addStatusEffect(new StatusEffectInstance(statusEffect, -1, 0, false, false));
        }
        return 1;
    }
}
