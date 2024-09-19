package com.tatnux.crafter.modules.crafter.packet;

import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import net.minecraft.network.FriendlyByteBuf;

public record SelectRecipePacket(byte slot, boolean reset) implements CrafterPacket {

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeByte(this.slot);
        friendlyByteBuf.writeBoolean(this.reset);
    }

    public static SelectRecipePacket create(FriendlyByteBuf friendlyByteBuf) {
        return new SelectRecipePacket(friendlyByteBuf.readByte(), friendlyByteBuf.readBoolean());
    }

    @Override
    public void handleMenu(SmartCrafterMenu menu) {
        if (this.reset) {
            menu.contentHolder.reset(this.slot);
        } else {
            menu.contentHolder.select(this.slot);
        }
    }
}
