package org.aussiebox.circuit_core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
//? 1.21.11 {
/*import net.minecraft.command.DefaultPermissions;
import net.minecraft.command.permission.PermissionCheck;
*///? }
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.exclusive.item.PlayerExclusiveItemsPersistentState;
import org.aussiebox.circuit_core.helper.PlayerExclusiveItemHelper;
import org.aussiebox.circuit_core.util.ExclusiveItemHolder;

import java.util.Optional;

public class PlayerExclusiveItemsCommand {
    //? 1.21.11
    //public static final PermissionCheck PERMISSION_CHECK = new PermissionCheck.Require(DefaultPermissions.GAMEMASTERS);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(
                CommandManager.literal("exclusive_item")
                        //? 1.21.11
                        //.requires(CommandManager.requirePermissionLevel(PERMISSION_CHECK))
                        //? 1.21.8 && 1.21.10
                        //.requires(CommandManager.requirePermissionLevel(2))
                        //? 1.21.1
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("item", new IdentifierArgumentType())
                                        .suggests((context, builder) -> {
                                            PlayerExclusiveItemHelper.getItems().forEach(key -> builder.suggest(key.getValue().toString()));
                                            return builder.buildFuture();
                                        })
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(PlayerExclusiveItemsCommand::addPlayer)
                                        )
                                )
                        )
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("item", new IdentifierArgumentType())
                                        .suggests((context, builder) -> {
                                            PlayerExclusiveItemHelper.getItems().forEach(key -> builder.suggest(key.getValue().toString()));
                                            return builder.buildFuture();
                                        })
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(PlayerExclusiveItemsCommand::removePlayer)
                                        )
                                )
                        )
        );
    }

    public static int addPlayer(CommandContext<ServerCommandSource> context) {
        try {
            ServerWorld world = context.getSource().getWorld();
            PlayerExclusiveItemsPersistentState state = PlayerExclusiveItemsPersistentState.getServerState(world);
            PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
            Item item = Registries.ITEM.get(IdentifierArgumentType.getIdentifier(context, "item"));
            Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(item);
            if (key.isEmpty() || item == Items.AIR) {
                context.getSource().sendError(Text.translatable("command.circuit_core.exclusive_item.add.invalid_item"));
                return 1;
            }

            state.addPlayer(player, key.get());
            if (player instanceof ExclusiveItemHolder holder) holder.circuitCore$allowItem(key.get());
            return 1;
        } catch (Exception e) {
            CircuitCore.LOGGER.error("Error adding exclusive item permission to player: {}", e.toString());
        }
        return 0;
    }

    public static int removePlayer(CommandContext<ServerCommandSource> context) {
        try {
            ServerWorld world = context.getSource().getWorld();
            PlayerExclusiveItemsPersistentState state = PlayerExclusiveItemsPersistentState.getServerState(world);
            PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
            Item item = Registries.ITEM.get(IdentifierArgumentType.getIdentifier(context, "item"));
            Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(item);
            if (key.isEmpty() || item == Items.AIR) {
                context.getSource().sendError(Text.translatable("command.circuit_core.exclusive_item.add.invalid_item"));
                return 1;
            }

            state.removePlayer(player, key.get());
            if (player instanceof ExclusiveItemHolder holder) holder.circuitCore$disallowItem(key.get());
            return 1;
        } catch (Exception e) {
            CircuitCore.LOGGER.error("Error removing exclusive item permission from player: {}", e.toString());
        }
        return 0;
    }
}
