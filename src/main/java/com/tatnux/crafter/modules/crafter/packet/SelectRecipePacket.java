package com.tatnux.crafter.modules.crafter.packet;

import com.tatnux.crafter.modules.crafter.blocks.CrafterMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public record SelectRecipePacket(byte slot) implements CrafterPacket {

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeByte(slot);
    }

    public static SelectRecipePacket create(FriendlyByteBuf friendlyByteBuf) {
        return new SelectRecipePacket(friendlyByteBuf.readByte());
    }

    @Override
    public void handleMenu(CrafterMenu menu) {
        menu.contentHolder.select(this.slot);
    }
}
