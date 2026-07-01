package org.aussiebox.circuit_core.helper.item;

import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ItemBuilder<I extends Item> {
    @Getter protected final Identifier itemId;
    @Getter protected final Function<Item.Settings, I> factory;
    @Getter protected final Item.Settings settings;
    @Getter @Nullable protected final Identifier groupId;
    @Nullable protected I builtItem = null;

    /// Creates a new ItemBuilder that will become an {@link Item Item} with default {@link net.minecraft.item.Item.Settings Settings} and no {@link net.minecraft.item.ItemGroup ItemGroup}.
    public ItemBuilder(Identifier itemId, Function<Item.Settings, I> factory) {
        this.itemId = itemId;
        this.factory = factory;
        this.settings = new Item.Settings();
        this.groupId = null;
    }

    /// Creates a new ItemBuilder that will become an {@link Item Item} with default {@link net.minecraft.item.Item.Settings Settings} and the given {@link net.minecraft.item.ItemGroup ItemGroup}.<br>
    /// Passing {@code null} as the {@code groupId} will add the item to the {@code {Item Namespace}:{Item Namespace}} {@link net.minecraft.item.ItemGroup ItemGroup}.
    public ItemBuilder(Identifier itemId, Function<Item.Settings, I> factory, @Nullable Identifier groupId) {
        this.itemId = itemId;
        this.factory = factory;
        this.settings = new Item.Settings();
        this.groupId = groupId == null ? Identifier.of(itemId.getNamespace(), itemId.getNamespace()) : groupId;
    }

    /// Creates a new ItemBuilder that will become an {@link Item Item} with the given {@link net.minecraft.item.Item.Settings Settings} and no {@link net.minecraft.item.ItemGroup ItemGroup}.
    public ItemBuilder(Identifier itemId, Function<Item.Settings, I> factory, Item.Settings settings) {
        this.itemId = itemId;
        this.factory = factory;
        this.settings = settings;
        this.groupId = null;
    }

    /// Creates a new ItemBuilder that will become an {@link Item Item} with the given {@link net.minecraft.item.Item.Settings Settings} and the given {@link net.minecraft.item.ItemGroup ItemGroup}.<br>
    /// Passing {@code null} as the {@code groupId} will add the item to the {@code {Item Namespace}:{Item Namespace}} {@link net.minecraft.item.ItemGroup ItemGroup}.
    public ItemBuilder(Identifier itemId, Function<Item.Settings, I> factory, Item.Settings settings, @Nullable Identifier groupId) {
        this.itemId = itemId;
        this.factory = factory;
        this.settings = settings;
        this.groupId = groupId == null ? Identifier.of(itemId.getNamespace(), itemId.getNamespace()) : groupId;
    }

    public I build() {
        if (builtItem != null) return builtItem;
        builtItem = factory.apply(settings);

        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, itemId);
        if (!Registries.ITEM.contains(key)) builtItem = Registry.register(Registries.ITEM, itemId, factory.apply(settings));
        
        return builtItem;
    }
}
