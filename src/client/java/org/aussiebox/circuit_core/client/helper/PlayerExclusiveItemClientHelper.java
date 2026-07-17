package org.aussiebox.circuit_core.client.helper;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import org.aussiebox.circuit_core.client.CircuitCoreClient;
import org.aussiebox.circuit_core.helper.PlayerExclusiveItemHelper;

import java.util.Optional;

public class PlayerExclusiveItemClientHelper {
    public static boolean playerCanGet(Item item) {
        Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(item);
        if (key.isEmpty() || !PlayerExclusiveItemHelper.canBeExclusive(item)) return true;
        return CircuitCoreClient.allowedItems.contains(key.get());
    }
}
