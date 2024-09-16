package com.tatnux.crafter.lib.module;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public interface IModule {

    void init(FMLCommonSetupEvent var1);

    void initClient(FMLClientSetupEvent var1);

    void initConfig(IEventBus var1);

    default void initDataGen(GatherDataEvent dataGen) {
    }

}
