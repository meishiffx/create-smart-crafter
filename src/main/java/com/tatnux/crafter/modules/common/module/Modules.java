package com.tatnux.crafter.modules.common.module;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;


public class Modules {
    private final List<IModule> modules = new ArrayList<>();

    public void register(IModule module) {
        this.modules.add(module);
    }

    public void init(FMLCommonSetupEvent event) {
        this.modules.forEach((m) -> {
            m.init(event);
        });
    }

    public void initClient(FMLClientSetupEvent event) {
        this.modules.forEach((m) -> {
            m.initClient(event);
        });
    }

    public void initConfig(IEventBus bus) {
        this.modules.forEach((iModule) -> {
            iModule.initConfig(bus);
        });
    }

    public void dataGen(GatherDataEvent dataGen) {
        this.modules.forEach((m) -> {
            m.initDataGen(dataGen);
        });
    }
}