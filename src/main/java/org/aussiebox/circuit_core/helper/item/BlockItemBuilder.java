package org.aussiebox.circuit_core.helper.item;

import lombok.Getter;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class BlockItemBuilder<I extends BlockItem> extends ItemBuilder<I> {
    /// Creates a new ItemBuilder that will become a {@link BlockItem BlockItem} with default {@link Item.Settings Settings} and no {@link net.minecraft.item.ItemGroup ItemGroup}.
    public BlockItemBuilder(Identifier itemId, Function<Item.Settings, I> factory) {
        super(itemId, factory);
    }

    /// Creates a new ItemBuilder that will become a {@link BlockItem BlockItem} with default {@link Item.Settings Settings} and the given {@link net.minecraft.item.ItemGroup ItemGroup}.<br>
    /// Passing {@code null} as the {@code groupId} will add the item to the {@code {Item Namespace}:{Item Namespace}} {@link net.minecraft.item.ItemGroup ItemGroup}.
    public BlockItemBuilder(Identifier itemId, Function<Item.Settings, I> factory, @Nullable Identifier groupId) {
        super(itemId, factory, groupId);
    }

    /// Creates a new ItemBuilder that will become a {@link BlockItem BlockItem} with the given {@link Item.Settings Settings} and no {@link net.minecraft.item.ItemGroup ItemGroup}.
    public BlockItemBuilder(Identifier itemId, Function<Item.Settings, I> factory, Item.Settings settings) {
        super(itemId, factory, settings);
    }

    /// Creates a new ItemBuilder that will become a {@link BlockItem BlockItem} with the given {@link Item.Settings Settings} and the given {@link net.minecraft.item.ItemGroup ItemGroup}.<br>
    /// Passing {@code null} as the {@code groupId} will add the item to the {@code {Item Namespace}:{Item Namespace}} {@link net.minecraft.item.ItemGroup ItemGroup}.
    public BlockItemBuilder(Identifier itemId, Function<Item.Settings, I> factory, Item.Settings settings, @Nullable Identifier groupId) {
        super(itemId, factory, settings, groupId);
    }

    public I build() {
        if (builtItem != null) return builtItem;

        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, itemId);
        //? >= 1.21.8
        //settings.registryKey(key);
        if (!Registries.ITEM.contains(key)) builtItem = Registry.register(Registries.ITEM, itemId, factory.apply(settings));

        return builtItem;
    }
}
