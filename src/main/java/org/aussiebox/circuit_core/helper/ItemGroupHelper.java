package org.aussiebox.circuit_core.helper;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;

import java.util.ArrayList;
import java.util.List;

public class ItemGroupHelper {
    protected static Object2ObjectOpenHashMap<Identifier, List<ItemStack>> entryMap = new Object2ObjectOpenHashMap<>();
    protected static Object2ObjectOpenHashMap<Identifier, ItemGroup> groupIDs = new Object2ObjectOpenHashMap<>();

    public static void registerGroup(ItemGroup group, Identifier id) {
        groupIDs.put(id, Registry.register(Registries.ITEM_GROUP, id, group));
        if (!entryMap.containsKey(id)) entryMap.put(id, new ArrayList<>());
    }

    public static void addEntry(Identifier groupID, ItemStack item) {
        if (!groupIDs.containsKey(groupID)) {
            registerGroup(FabricItemGroup.builder().displayName(Text.literal("Unregistered ItemGroup")).icon(Items.BARRIER::getDefaultStack).build(), groupID);
            CircuitCore.LOGGER.warn("Item {} was registered into an unregistered group, creating default group", item.getRegistryEntry().getIdAsString());
        }

        List<ItemStack> list = entryMap.getOrDefault(groupID, new ArrayList<>());
        list.add(item);
        entryMap.put(groupID, list);
    }

    public static void init() {
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, entries) -> {
            Identifier groupId = Registries.ITEM_GROUP.getId(group);
            if (groupId != null) CircuitCore.LOGGER.info(groupId.toString());

            if (groupId != null && groupIDs.containsKey(groupId)) {
                List<ItemStack> items = entryMap.getOrDefault(groupId, new ArrayList<>());
                entries.addAll(items);
            }
        });
    }
}