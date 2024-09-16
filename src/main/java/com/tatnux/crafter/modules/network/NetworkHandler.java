package com.tatnux.crafter.modules.network;

import com.tatnux.crafter.SimplyCrafter;
import com.tatnux.crafter.jei.PacketSendRecipe;
import com.tatnux.crafter.modules.crafter.packet.SelectRecipePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "2";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SimplyCrafter.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void init() {
        INSTANCE.registerMessage(0, PacketSendRecipe.class, PacketSendRecipe::write, PacketSendRecipe::create, PacketSendRecipe::handle);
        INSTANCE.registerMessage(1, SelectRecipePacket.class, SelectRecipePacket::write, SelectRecipePacket::create, SelectRecipePacket::handle);
    }

    public static void sendRecipeToServer(PacketSendRecipe packet) {
        INSTANCE.sendToServer(packet);
    }

    public static void selectSlot(byte slot) {
        INSTANCE.sendToServer(new SelectRecipePacket(slot));
    }
}
