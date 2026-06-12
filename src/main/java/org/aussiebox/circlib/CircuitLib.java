package org.aussiebox.circlib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.aussiebox.circlib.helper.ItemGroupHelper;
import org.aussiebox.circlib.helper.ItemHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitLib implements ModInitializer {

    public static final String MOD_ID = "circlib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ItemGroupHelper.init();

        ItemGroupHelper.registerGroup(FabricItemGroup.builder().displayName(Text.literal("test")).build(), id(MOD_ID));
        ItemHelper.registerItem(
                id("test"),
                Item::new,
                new Item.Settings(),
                null
        );
    }
}