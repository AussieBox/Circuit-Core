package org.aussiebox.circuit_core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//? 1.21.11
//import net.minecraft.command.DefaultPermissions;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.CircuitCoreConstants;
import org.aussiebox.circuit_core.network.SetAnimationS2CPayload;
import org.aussiebox.circuit_core.network.SetStackAnimationS2CPayload;
import org.aussiebox.circuit_core.pal.PALHelper;
import org.aussiebox.circuit_core.pal.animation.PALStackAnimation;

import java.util.Objects;

public class MainCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal(CircuitCore.MOD_ID)
                        .then(CommandManager.literal("animation")
                                //? <1.21.11
                                .requires(context -> context.hasPermissionLevel(2))
                                //? 1.21.11
                                //.requires(context -> context.getPermissions().hasPermission(DefaultPermissions.GAMEMASTERS))
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .then(CommandManager.argument("controller", IdentifierArgumentType.identifier())
                                                .then(CommandManager.argument("animation", IdentifierArgumentType.identifier())
                                                        .executes(MainCommand::setAnimation)
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("stack_animation")
                                //? <1.21.11
                                .requires(context -> context.hasPermissionLevel(2))
                                //? 1.21.11
                                //.requires(context -> context.getPermissions().hasPermission(DefaultPermissions.GAMEMASTERS))
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .then(CommandManager.argument("controller", IdentifierArgumentType.identifier())
                                                .then(CommandManager.argument("animation", IdentifierArgumentType.identifier())
                                                        .executes(MainCommand::setStackAnimation)
                                                )
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
            if (Objects.equals(animation, Identifier.ofVanilla("null"))) animation = CircuitCoreConstants.NO_ANIMATION;

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
            ItemStack stack = null;
            String hand = null;
            if (Objects.equals(animation, Identifier.ofVanilla("null"))) animation = CircuitCoreConstants.NO_ANIMATION;
            else {
                PALStackAnimation stackAnimation = PALHelper.getAnimation(PALStackAnimation.class, controller, animation);
                if (stackAnimation == null) {
                    context.getSource().sendError(Text.translatable("command.circuit_core.main.stack_animation.incorrect_type"));
                    return 1;
                }
                if (player.getMainHandStack().isOf(stackAnimation.expectedItem.get())) {
                    stack = player.getMainHandStack();
                    hand = "MAIN_HAND";
                }
                else if (player.getOffHandStack().isOf(stackAnimation.expectedItem.get())) {
                    stack = player.getOffHandStack();
                    hand = "OFF_HAND";
                }
            }

            ServerPlayNetworking.send(context.getSource().getPlayer(), new SetStackAnimationS2CPayload(player.getUuid(), controller, animation, stack, hand));
        } catch (Exception e) {
            CircuitCore.LOGGER.error("Failed to set animation for player: {}", e.getMessage());
            return 0;
        }

        return 1;
    }
}
