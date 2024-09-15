package com.tatnux.crafter;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tatnux.crafter.modules.common.module.Modules;
import com.tatnux.crafter.modules.crafter.CrafterModule;
import com.tatnux.crafter.modules.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(SimplyCrafter.MOD_ID)
public class SimplyCrafter {

    public static final String MOD_ID = "simplycrafter";

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

//    private static final RegistryEntry<CreativeModeTab> TAB = REGISTRATE.defaultCreativeTab(builder -> builder
//            .icon(() -> new ItemStack(CrafterModule.CRAFTER))
//            .title(Component.literal("Simply Crafter"))
//    ).register();

    private final Modules modules = new Modules();

    public SimplyCrafter() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Dist dist = FMLEnvironment.dist;

        bus.addListener(this::setup);

        REGISTRATE.registerEventListeners(bus);
        setupModules(bus, dist);
    }

    private void setupModules(IEventBus bus, Dist dist) {
        modules.register(new CrafterModule());
    }

    private void setup(final FMLCommonSetupEvent event) {
        NetworkHandler.init();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

}
