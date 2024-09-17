package com.tatnux.crafter.modules.crafter.blocks.inventory;

import com.tatnux.crafter.modules.crafter.blocks.CrafterBlockEntity;
import com.tatnux.crafter.modules.crafter.blocks.CrafterMenu;
import com.tatnux.crafter.modules.crafter.blocks.slots.ItemValidator;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class CrafterInventory extends ItemStackHandler implements ItemValidator {

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

    public boolean canAutomationInsert(int slot) {
        return slot >= CrafterMenu.CONTAINER_START && slot < CrafterMenu.CONTAINER_START + CrafterMenu.CONTAINER_SIZE;
    }

    public boolean canAutomationExtract(int slot) {
        return slot >= CrafterMenu.RESULT_SLOT_START && slot < CrafterMenu.RESULT_SLOT_START + CrafterMenu.RESULT_SLOT_SIZE;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return this.blockEntity.isItemValidForSlot(slot, stack);
    }
}
