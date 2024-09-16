package com.tatnux.crafter.modules.crafter.blocks.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WorkingCraftingInventory extends TransientCraftingContainer {

    public WorkingCraftingInventory() {
        super(new AbstractContainerMenu(null, -1) {
            @Override
            public @NotNull ItemStack quickMoveStack(Player player, int i) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(Player player) {
                return false;
            }
        }, 3, 3);
    }


}
