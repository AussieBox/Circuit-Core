package org.aussiebox.circuit_core.helper.itemgroup;

import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public interface ItemGroupSupplier {
    Identifier getGroupId();
    ItemGroup.Builder getGroupBuilder();
}
