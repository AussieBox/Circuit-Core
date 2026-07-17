package org.aussiebox.circuit_core.util;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;

public interface ExclusiveItemHolder {
    void circuitCore$allowItem(Item item);
    void circuitCore$disallowItem(Item item);
    boolean circuitCore$itemAllowed(Item item);
    void circuitCore$allowItem(RegistryKey<Item> item);
    void circuitCore$disallowItem(RegistryKey<Item> item);
    boolean circuitCore$itemAllowed(RegistryKey<Item> item);
    void circuitCore$syncToClient();
}
