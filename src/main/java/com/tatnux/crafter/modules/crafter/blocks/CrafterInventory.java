package com.tatnux.crafter.modules.crafter.blocks;

import net.minecraftforge.items.ItemStackHandler;

public class CrafterInventory extends ItemStackHandler {

    private final CrafterBlockEntity blockEntity;

    public CrafterInventory(CrafterBlockEntity blockEntity) {
        super(18);
        this.blockEntity = blockEntity;
    }

}
