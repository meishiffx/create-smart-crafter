package com.tatnux.crafter.modules.network;

import com.tatnux.crafter.SimplyCrafter;
import com.tatnux.crafter.jei.PacketSendRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SimplyCrafter.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void init() {
        INSTANCE.registerMessage(0, PacketSendRecipe.class, PacketSendRecipe::write, PacketSendRecipe::create, PacketSendRecipe::handle);
    }

    public static void sendRecipeToServer(PacketSendRecipe packet) {
        INSTANCE.sendToServer(packet);
    }
}
