package com.tatnux.crafter.modules.crafter.packet;

import com.tatnux.crafter.modules.crafter.blocks.CrafterMenu;
import com.tatnux.crafter.modules.crafter.data.CraftMode;
import net.minecraft.network.FriendlyByteBuf;

public record SelectCraftModePacket(CraftMode mode) implements CrafterPacket {

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeByte(mode.ordinal());
    }

    public static SelectCraftModePacket create(FriendlyByteBuf friendlyByteBuf) {
        return new SelectCraftModePacket(CraftMode.values()[friendlyByteBuf.readByte()]);
    }

    @Override
    public void handleMenu(CrafterMenu menu) {
        menu.contentHolder.setCraftMode(this.mode);
    }
}
