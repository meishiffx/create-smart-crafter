package com.tatnux.crafter.modules.crafter;


import com.simibubi.create.foundation.data.SharedProperties;
import com.tatnux.crafter.modules.common.module.IModule;
import com.tatnux.crafter.modules.crafter.blocks.CrafterBlock;
import com.tatnux.crafter.modules.crafter.blocks.CrafterBlockEntity;
import com.tatnux.crafter.modules.crafter.blocks.CrafterMenu;
import com.tatnux.crafter.modules.crafter.blocks.CrafterScreen;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static com.tatnux.crafter.SimplyCrafter.REGISTRATE;


public class CrafterModule implements IModule {

    public static final BlockEntry<CrafterBlock> CRAFTER = REGISTRATE
            .block("crafter", CrafterBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .tag()
            .simpleItem()
            .register();

    public static final BlockEntityEntry<CrafterBlockEntity> CRAFTER_BLOCK_ENTITY = REGISTRATE
            .blockEntity("crafter", CrafterBlockEntity::new)
            .validBlocks(CRAFTER)
            .register();

    public static final MenuEntry<CrafterMenu> CRAFTER_MENU = REGISTRATE
            .menu("crafter", CrafterMenu::new, () -> CrafterScreen::new)
            .register();


    @Override
    public void init(FMLCommonSetupEvent fmlCommonSetupEvent) {

    }

    @Override
    public void initClient(FMLClientSetupEvent fmlClientSetupEvent) {

    }

    @Override
    public void initConfig(IEventBus iEventBus) {

    }
}
