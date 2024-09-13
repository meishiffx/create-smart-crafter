package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import com.tatnux.crafter.modules.crafter.CrafterModule;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class CrafterMenu extends MenuBase<CrafterBlockEntity> {

    public CrafterMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public CrafterMenu(MenuType<?> type, int id, Inventory inv, CrafterBlockEntity be) {
        super(type, id, inv, be);
//        be.startOpen(player);

    }

    public static CrafterMenu create(int id, Inventory inv, CrafterBlockEntity be) {
        return new CrafterMenu(CrafterModule.CRAFTER_MENU.get(), id, inv, be);
    }

    @Override
    protected CrafterBlockEntity createOnClient(FriendlyByteBuf extraData) {
        return null;
    }

    @Override
    protected void initAndReadInventory(CrafterBlockEntity contentHolder) {

    }

    @Override
    protected void addSlots() {
        CrafterInventory inventory = contentHolder.inventory;

//        int x = 23;
//        int y = 22;
//        for (int row = 0; row < 2; ++row)
//            for (int col = 0; col < 9; ++col)
//                addSlot(new SlotItemHandler(inventory, col + row * 9, x + col * 18, y + row * 18));
//
//        this.addPlayerSlots(8, 165);
    }

    @Override
    protected void saveData(CrafterBlockEntity contentHolder) {

    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return null;
    }
}
