package com.tatnux.crafter.modules.crafter.blocks.slots;

import net.minecraft.world.entity.player.Player;

public class InfoSlot extends ResultSlot{

    public InfoSlot(ItemValidator itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }

}
