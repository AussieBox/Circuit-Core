package org.aussiebox.circlib.helper;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ItemHelper {
    //? 1.21.1 {
    public static <I extends Item> I registerItem(Identifier id, Function<Item.Settings, I> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        I item = Registry.register(Registries.ITEM, itemKey, itemFactory.apply(settings));
        return item;
    }

    public static <I extends BlockItem> I registerBlockItem(Identifier id, Function<Item.Settings, I> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        I item = Registry.register(Registries.ITEM, itemKey, itemFactory.apply(settings));
        return item;
    }

    public static <I extends Item> I registerItem(Identifier id, Function<Item.Settings, I> itemFactory, Item.Settings settings, @Nullable Identifier groupId) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        I item = Registry.register(Registries.ITEM, itemKey, itemFactory.apply(settings));
        ItemGroupHelper.addEntry(groupId == null ? Identifier.of(id.getNamespace(), id.getNamespace()) : groupId, item.getDefaultStack());
        return item;
    }

    public static <I extends BlockItem> I registerBlockItem(Identifier id, Function<Item.Settings, I> itemFactory, Item.Settings settings, @Nullable Identifier groupId) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        I item = Registry.register(Registries.ITEM, itemKey, itemFactory.apply(settings));
        ItemGroupHelper.addEntry(groupId == null ? Identifier.of(id.getNamespace(), id.getNamespace()) : groupId, item.getDefaultStack());
        return item;
    }
    //? }

    //? >=1.21.8 {
    /*public static <I extends Item> I registerItem(Identifier id, Function<Item.Settings, I> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        I item = Registry.register(Registries.ITEM, itemKey, itemFactory.apply(settings.registryKey(itemKey)));
        return item;
    }

    public static <I extends BlockItem> I registerBlockItem(Identifier id, Function<Item.Settings, I> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        I item = Registry.register(Registries.ITEM, itemKey, itemFactory.apply(settings.registryKey(itemKey).useBlockPrefixedTranslationKey()));
        return item;
    }

    public static <I extends Item> I registerItem(Identifier id, Function<Item.Settings, I> itemFactory, Item.Settings settings, @Nullable Identifier groupId) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        I item = Registry.register(Registries.ITEM, itemKey, itemFactory.apply(settings.registryKey(itemKey)));
        ItemGroupHelper.addEntry(groupId == null ? Identifier.of(id.getNamespace(), id.getNamespace()) : groupId, item.getDefaultStack());
        return item;
    }

    public static <I extends BlockItem> I registerBlockItem(Identifier id, Function<Item.Settings, I> itemFactory, Item.Settings settings, @Nullable Identifier groupId) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        I item = Registry.register(Registries.ITEM, itemKey, itemFactory.apply(settings.registryKey(itemKey).useBlockPrefixedTranslationKey()));
        ItemGroupHelper.addEntry(groupId == null ? Identifier.of(id.getNamespace(), id.getNamespace()) : groupId, item.getDefaultStack());
        return item;
    }
    *///? }

}
