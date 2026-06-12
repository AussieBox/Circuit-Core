package org.aussiebox.circlib.helper;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class BlockHelper {
    //? 1.21.1 {
    public static <B extends Block> B register(Identifier id, Function<AbstractBlock.Settings, B> blockFactory, AbstractBlock.Settings settings) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        B block = blockFactory.apply(settings);
        Registry.register(Registries.BLOCK, blockKey, block);
        return block;
    }

    public static <B extends Block> B registerWithItem(Identifier id, Function<AbstractBlock.Settings, B> blockFactory, AbstractBlock.Settings settings) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        B block = blockFactory.apply(settings);

        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        BlockItem blockItem = new BlockItem(block, new Item.Settings());
        Registry.register(Registries.ITEM, itemKey, blockItem);

        Registry.register(Registries.BLOCK, blockKey, block);
        return block;
    }

    public static <B extends Block> B registerWithItem(Identifier id, Function<AbstractBlock.Settings, B> blockFactory, AbstractBlock.Settings settings, @Nullable Identifier groupId) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        B block = blockFactory.apply(settings);

        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        BlockItem blockItem = new BlockItem(block, new Item.Settings());
        Registry.register(Registries.ITEM, itemKey, blockItem);
        ItemGroupHelper.addEntry(groupId == null ? Identifier.of(id.getNamespace(), id.getNamespace()) : groupId, blockItem.getDefaultStack());

        Registry.register(Registries.BLOCK, blockKey, block);
        return block;
    }
    //? }

    //? >=1.21.8 {
    /*public static <B extends Block> B register(Identifier id, Function<AbstractBlock.Settings, B> blockFactory, AbstractBlock.Settings settings) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        B block = blockFactory.apply(settings.registryKey(blockKey));
        Registry.register(Registries.BLOCK, blockKey, block);
        return block;
    }

    public static <B extends Block> B registerWithItem(Identifier id, Function<AbstractBlock.Settings, B> blockFactory, AbstractBlock.Settings settings) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        B block = blockFactory.apply(settings.registryKey(blockKey));

        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey());
        Registry.register(Registries.ITEM, itemKey, blockItem);

        Registry.register(Registries.BLOCK, blockKey, block);
        return block;
    }
    *///? }
}
