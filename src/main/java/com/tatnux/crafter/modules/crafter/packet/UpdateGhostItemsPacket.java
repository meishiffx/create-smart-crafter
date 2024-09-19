package com.tatnux.crafter.modules.crafter.packet;

import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import net.minecraft.network.FriendlyByteBuf;

public record UpdateGhostItemsPacket(boolean reset) implements CrafterPacket {

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(this.reset);
    }

    public static UpdateGhostItemsPacket create(FriendlyByteBuf friendlyByteBuf) {
        return new UpdateGhostItemsPacket(friendlyByteBuf.readBoolean());
    }

    @Override
    public void handleMenu(SmartCrafterMenu menu) {
        menu.contentHolder.updateGhostItems(this.reset);
    }
}
