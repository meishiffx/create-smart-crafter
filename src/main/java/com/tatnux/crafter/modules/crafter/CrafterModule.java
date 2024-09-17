package com.tatnux.crafter.modules.crafter;


import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.crafter.CrafterCTBehaviour;
import com.simibubi.create.content.kinetics.crafter.ShaftlessCogwheelInstance;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tatnux.crafter.lib.module.IModule;
import com.tatnux.crafter.modules.crafter.blocks.*;
import com.tatnux.crafter.modules.crafter.client.CrafterScreen;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.tatnux.crafter.SimplyCrafter.REGISTRATE;


public class CrafterModule implements IModule {

    @SuppressWarnings("removal")
    public static final BlockEntry<CrafterBlock> CRAFTER = REGISTRATE
            .block("crafter", CrafterBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_YELLOW))
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .transform(BlockStressDefaults.setImpact(5.0))
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntityEntry<CrafterBlockEntity> CRAFTER_BLOCK_ENTITY = REGISTRATE
            .blockEntity("crafter", CrafterBlockEntity::new)
            .instance(() -> ShaftlessCogwheelInstance::new, false)
            .validBlocks(CRAFTER)
            .renderer(() -> CrafterRenderer::new)
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
