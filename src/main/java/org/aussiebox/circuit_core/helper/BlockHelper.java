package org.aussiebox.circuit_core.helper;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.aussiebox.circuit_core.helper.item.BlockItemBuilder;
import org.aussiebox.circuit_core.helper.item.ItemRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class BlockHelper {
    public static <B extends Block> B register(Identifier id, Function<AbstractBlock.Settings, B> blockFactory, AbstractBlock.Settings settings) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        //? 1.21.1
        //B block = blockFactory.apply(settings);
        //? >=1.21.8
        B block = blockFactory.apply(settings.registryKey(blockKey));
        Registry.register(Registries.BLOCK, blockKey, block);
        return block;
    }

    public static <B extends Block> B registerWithItem(Identifier id, Function<AbstractBlock.Settings, B> blockFactory, AbstractBlock.Settings settings) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        //? 1.21.1
        //B block = blockFactory.apply(settings);
        //? >=1.21.8
        B block = blockFactory.apply(settings.registryKey(blockKey));

        ItemRegistry.register(
                new BlockItemBuilder<>(
                        id,
                        (settings1 -> new BlockItem(block, settings1)),
                        new Item.Settings()
                )
        );

        Registry.register(Registries.BLOCK, blockKey, block);
        return block;
    }

    public static <B extends Block> B registerWithItem(Identifier id, Function<AbstractBlock.Settings, B> blockFactory, AbstractBlock.Settings settings, @Nullable Identifier groupId) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        //? 1.21.1
        //B block = blockFactory.apply(settings);
        //? >=1.21.8
        B block = blockFactory.apply(settings.registryKey(blockKey));

        ItemRegistry.register(
                new BlockItemBuilder<>(
                        id,
                        (settings1 -> new BlockItem(block, settings1)),
                        new Item.Settings(),
                        groupId
                )
        );

        Registry.register(Registries.BLOCK, blockKey, block);
        return block;
    }

    public static <B extends Block, I extends BlockItem> Pair<B, BlockItemBuilder<I>> registerWithCustomItem(Identifier id, Function<AbstractBlock.Settings, B> blockFactory, AbstractBlock.Settings settings,  Function<Item.Settings, I> itemFactory, Item.Settings itemSettings) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        //? 1.21.1
        //B block = blockFactory.apply(settings);
        //? >=1.21.8
        B block = blockFactory.apply(settings.registryKey(blockKey));

        BlockItemBuilder<I> item = ItemRegistry.register(
                new BlockItemBuilder<>(
                        id,
                        itemFactory,
                        itemSettings
                )
        );

        Registry.register(Registries.BLOCK, blockKey, block);
        return new Pair<>(block, item);
    }

    public static <B extends Block, I extends BlockItem> Pair<B, BlockItemBuilder<I>> registerWithCustomItem(Identifier id, Function<AbstractBlock.Settings, B> blockFactory, AbstractBlock.Settings settings, Function<Item.Settings, I> itemFactory, Item.Settings itemSettings, @Nullable Identifier groupId) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        //? 1.21.1
        //B block = blockFactory.apply(settings);
        //? >=1.21.8
        B block = blockFactory.apply(settings.registryKey(blockKey));

        BlockItemBuilder<I> item = ItemRegistry.register(
                new BlockItemBuilder<>(
                        id,
                        itemFactory,
                        itemSettings,
                        groupId
                )
        );

        Registry.register(Registries.BLOCK, blockKey, block);
        return new Pair<>(block, item);
    }
}
