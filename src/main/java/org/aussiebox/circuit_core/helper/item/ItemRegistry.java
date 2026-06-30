package org.aussiebox.circuit_core.helper.item;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {
    private static final List<ItemBuilder<?>> builders = new ArrayList<>();
    private static final Object2ObjectOpenHashMap<Identifier, List<Item>> groupedItems = new Object2ObjectOpenHashMap<>();

    public static <I extends Item> ItemBuilder<I> register(ItemBuilder<I> builder) {
        builders.add(builder);
        return builder;
    }

    public static <I extends BlockItem> BlockItemBuilder<I> register(BlockItemBuilder<I> builder) {
        builders.add(builder);
        return builder;
    }

    public static void bake() {
        for (ItemBuilder<?> builder : builders) {
            Item item = builder.build();
            RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, builder.getItemId());
            if (!Registries.ITEM.contains(key)) Registry.register(Registries.ITEM, builder.getItemId(), item);

            Identifier groupId = builder.getGroupId();
            if (groupId != null) {
                groupedItems.computeIfAbsent(groupId, k -> new ArrayList<>()).add(item);
            }
        }

        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, entries) -> {
            Identifier currentGroupId = Registries.ITEM_GROUP.getId(group);
            if (currentGroupId != null && groupedItems.containsKey(currentGroupId)) {
                for (Item item : groupedItems.get(currentGroupId)) {
                    entries.add(item);
                }
            }
        });
    }
}
