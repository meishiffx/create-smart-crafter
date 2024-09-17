package com.tatnux.crafter.modules.network;

import com.tatnux.crafter.SimplyCrafter;
import com.tatnux.crafter.jei.PacketSendRecipe;
import com.tatnux.crafter.modules.crafter.data.CraftMode;
import com.tatnux.crafter.modules.crafter.packet.SelectCraftModePacket;
import com.tatnux.crafter.modules.crafter.packet.SelectRecipePacket;
import com.tatnux.crafter.modules.crafter.packet.SetKeepMode;
import com.tatnux.crafter.modules.crafter.packet.UpdateGhostItemsPacket;
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
        INSTANCE.registerMessage(2, SelectCraftModePacket.class, SelectCraftModePacket::write, SelectCraftModePacket::create, SelectCraftModePacket::handle);
        INSTANCE.registerMessage(3, SetKeepMode.class, SetKeepMode::write, SetKeepMode::create, SetKeepMode::handle);
        INSTANCE.registerMessage(4, UpdateGhostItemsPacket.class, UpdateGhostItemsPacket::write, UpdateGhostItemsPacket::create, UpdateGhostItemsPacket::handle);
    }

    public static void sendRecipeToServer(PacketSendRecipe packet) {
        INSTANCE.sendToServer(packet);
    }

    public static void resetRecipe(byte slot) {
        INSTANCE.sendToServer(new SelectRecipePacket(slot, true));
    }

    public static void selectSlot(byte slot) {
        INSTANCE.sendToServer(new SelectRecipePacket(slot, false));
    }

    public static void selectCraftMode(CraftMode mode) {
        INSTANCE.sendToServer(new SelectCraftModePacket(mode));
    }

    public static void setKeepMode(boolean keepMode) {
        INSTANCE.sendToServer(new SetKeepMode(keepMode));
    }

    public static void updateGhostItems(boolean reset) {
        INSTANCE.sendToServer(new UpdateGhostItemsPacket(reset));
    }
}
