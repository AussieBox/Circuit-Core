package org.aussiebox.circuit_core.exclusive.item;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
//? >= 1.21.8
//import net.minecraft.world.PersistentStateType;
import org.aussiebox.circuit_core.helper.PlayerExclusiveItemHelper;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerExclusiveItemsPersistentState extends PersistentState {
    public static final Codec<Map<RegistryKey<Item>, List<UUID>>> ITEMS_LIST_CODEC = Codec.unboundedMap(RegistryKey.createCodec(RegistryKeys.ITEM), Codec.list(Uuids.CODEC));
    //? 1.21.1 {
    public static final Type<PlayerExclusiveItemsPersistentState> TYPE = new Type<>(
            PlayerExclusiveItemsPersistentState::new,
            PlayerExclusiveItemsPersistentState::readNbt,
            null
    );
    //? }
    //? >=1.21.8 {
    /*public static final Codec<PlayerExclusiveItemsPersistentState> CODEC = ITEMS_LIST_CODEC.xmap(
            PlayerExclusiveItemsPersistentState::new,
            state -> state.items
    );

    public static final PersistentStateType<PlayerExclusiveItemsPersistentState> TYPE = new PersistentStateType<>(
            "exclusive_items",
            PlayerExclusiveItemsPersistentState::new,
            CODEC,
            null
    );
    *///? }

    private final Object2ObjectOpenHashMap<RegistryKey<Item>, List<UUID>> items;

    public PlayerExclusiveItemsPersistentState() {
        this.items = new Object2ObjectOpenHashMap<>();
    }

    public PlayerExclusiveItemsPersistentState(Object2ObjectOpenHashMap<RegistryKey<Item>, List<UUID>> items) {
        this.items = items;
    }

    public PlayerExclusiveItemsPersistentState(Map<RegistryKey<Item>, List<UUID>> items) {
        this.items = new Object2ObjectOpenHashMap<>(items);
    }

    public void addPlayer(PlayerEntity player, Item item) {
        Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(item);
        if (key.isEmpty() || !PlayerExclusiveItemHelper.canBeExclusive(item)) return;

        List<UUID> list = new ArrayList<>(items.getOrDefault(item, new ArrayList<>()));
        if (!list.contains(player.getUuid())) list.add(player.getUuid());
        items.put(key.get(), list);

        markDirty();
    }

    public void addPlayer(UUID player, Item item) {
        Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(item);
        if (key.isEmpty() || !PlayerExclusiveItemHelper.canBeExclusive(item)) return;

        List<UUID> list = new ArrayList<>(items.getOrDefault(item, new ArrayList<>()));
        if (!list.contains(player)) list.add(player);
        items.put(key.get(), list);

        markDirty();
    }

    public void addPlayer(PlayerEntity player, RegistryKey<Item> item) {
        if (!PlayerExclusiveItemHelper.canBeExclusive(item)) return;

        List<UUID> list = new ArrayList<>(items.getOrDefault(item, new ArrayList<>()));
        if (!list.contains(player.getUuid())) list.add(player.getUuid());
        items.put(item, list);

        markDirty();
    }

    public void removePlayer(PlayerEntity player, RegistryKey<Item> item) {
        if (!PlayerExclusiveItemHelper.canBeExclusive(item)) return;

        List<UUID> list = new ArrayList<>(items.getOrDefault(item, new ArrayList<>()));
        list.removeAll(Collections.singleton(player.getUuid()));
        items.put(item, list);

        markDirty();
    }

    public boolean playerCanGet(PlayerEntity player, Item item) {
        Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(item);
        if (key.isEmpty() || !PlayerExclusiveItemHelper.canBeExclusive(item)) return false;
        return items.getOrDefault(key.get(), new ArrayList<>()).contains(player.getUuid());
    }

    public boolean playerCanGet(UUID player, Item item) {
        Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(item);
        if (key.isEmpty() || !PlayerExclusiveItemHelper.canBeExclusive(item)) return false;
        return items.getOrDefault(key.get(), new ArrayList<>()).contains(player);
    }

    public List<RegistryKey<Item>> allPlayerCanGet(PlayerEntity player) {
        UUID uuid = player.getUuid();
        return items.entrySet().stream()
                .filter(entry -> entry.getValue().contains(uuid))
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<RegistryKey<Item>> allPlayerCanGet(UUID player) {
        return items.entrySet().stream()
                .filter(entry -> entry.getValue().contains(player))
                .map(Map.Entry::getKey)
                .toList();
    }

    public boolean exists(RegistryKey<Item> key) {
        return items.containsKey(key);
    }

    /// Gets the state from the given {@link ServerWorld ServerWorld}.<br>
    /// This is the only way default players can be applied.
    public static PlayerExclusiveItemsPersistentState getServerState(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        //? 1.21.1
        PlayerExclusiveItemsPersistentState state = manager.getOrCreate(TYPE, "player_exclusive_items");
        //? >=1.21.8
        //PlayerExclusiveItemsPersistentState state = manager.getOrCreate(TYPE);

        for (Map.Entry<RegistryKey<Item>, List<UUID>> entry : PlayerExclusiveItemHelper.getDefaultPlayers().entrySet()) {
            if (state.exists(entry.getKey())) continue;

            List<UUID> list = new ArrayList<>(state.items.getOrDefault(entry.getKey(), new ArrayList<>()));
            entry.getValue().forEach(uuid -> {
                if (!list.contains(uuid)) list.add(uuid);
            });
            state.items.put(entry.getKey(), list);

            state.markDirty();
        }

        return state;
    }

    //? 1.21.1 {
    @Override
    public NbtCompound writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        ITEMS_LIST_CODEC.encodeStart(NbtOps.INSTANCE, items).resultOrPartial().ifPresent(nbtElement -> tag.put("items", nbtElement));
        return tag;
    }

    public static PlayerExclusiveItemsPersistentState readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        return new PlayerExclusiveItemsPersistentState(new Object2ObjectOpenHashMap<>(ITEMS_LIST_CODEC.parse(NbtOps.INSTANCE, tag.get("items")).resultOrPartial().orElse(new Object2ObjectOpenHashMap<>())));
    }
    //? }
}
