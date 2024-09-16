package com.tatnux.crafter.modules.crafter.blocks;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class CrafterInventory extends ItemStackHandler {

    private final CrafterBlockEntity blockEntity;

    public CrafterInventory(CrafterBlockEntity blockEntity) {
        super(32);
        this.blockEntity = blockEntity;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        super.setStackInSlot(slot, stack);
        this.blockEntity.sendData();
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        this.blockEntity.sendData();
        this.blockEntity.setChanged();
    }
}
