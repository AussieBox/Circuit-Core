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
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.CircuitCoreConstants;
import org.aussiebox.circuit_core.network.SetAnimationS2CPayload;

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
}
