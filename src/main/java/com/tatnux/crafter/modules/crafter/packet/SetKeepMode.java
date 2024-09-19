package com.tatnux.crafter.modules.crafter.packet;

import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import net.minecraft.network.FriendlyByteBuf;

public record SetKeepMode(boolean keep) implements CrafterPacket {

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(this.keep);
    }

    public static SetKeepMode create(FriendlyByteBuf friendlyByteBuf) {
        return new SetKeepMode(friendlyByteBuf.readBoolean());
    }

    @Override
    public void handleMenu(SmartCrafterMenu menu) {
        menu.contentHolder.setKeepMode(this.keep);
    }
}
