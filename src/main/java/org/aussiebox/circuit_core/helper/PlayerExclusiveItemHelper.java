package org.aussiebox.circuit_core.helper;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;

import java.util.*;

public class PlayerExclusiveItemHelper {
    @Getter private static final List<RegistryKey<Item>> items = new ArrayList<>();
    @Getter private static final Object2ObjectOpenHashMap<RegistryKey<Item>, List<UUID>> defaultPlayers = new Object2ObjectOpenHashMap<>();

    public static void makeExclusive(Item item, UUID... defaultPlayers) {
        Registries.ITEM.getKey(item).ifPresent(key -> {
            if (!items.contains(key)) {
                items.add(key);
                PlayerExclusiveItemHelper.defaultPlayers.put(key, Arrays.stream(defaultPlayers).toList());
            }
        });
    }

    public static boolean canBeExclusive(Item item) {
        Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(item);
        return key.isPresent() && items.contains(key.get());
    }

    public static boolean canBeExclusive(RegistryKey<Item> item) {
        return items.contains(item);
    }
}
