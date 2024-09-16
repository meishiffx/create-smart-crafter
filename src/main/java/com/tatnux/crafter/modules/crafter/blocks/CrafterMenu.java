package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import com.tatnux.crafter.lib.menu.SlotItemHandlerFactory;
import com.tatnux.crafter.modules.crafter.CrafterModule;
import com.tatnux.crafter.modules.crafter.blocks.inventory.WorkingCraftingInventory;
import com.tatnux.crafter.modules.crafter.blocks.slots.InfoSlot;
import com.tatnux.crafter.modules.crafter.blocks.slots.ResultSlot;
import com.tatnux.crafter.modules.crafter.data.CrafterRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CrafterMenu extends MenuBase<CrafterBlockEntity> {

    public static final int CRAFT_RESULT_SLOT = 0;
    public static final int CRAFT_SLOT_START = CRAFT_RESULT_SLOT + 1;
    public static final int CRAFT_SLOTS_SIZE = 9;
    public static final int CONTAINER_START = CRAFT_SLOT_START + CRAFT_SLOTS_SIZE;
    public static final int CONTAINER_SIZE = 18;
    public static final int RESULT_SLOT_START = CONTAINER_START + CONTAINER_SIZE;
    public static final int RESULT_SLOT_SIZE = 4;
    public static final int PLAYER_SLOT_START = RESULT_SLOT_START + RESULT_SLOT_SIZE;

    public CrafterMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public CrafterMenu(MenuType<?> type, int id, Inventory inv, CrafterBlockEntity be) {
        super(type, id, inv, be);

    }

    public static CrafterMenu create(int id, Inventory inv, CrafterBlockEntity be) {
        return new CrafterMenu(CrafterModule.CRAFTER_MENU.get(), id, inv, be);
    }

    @Override
    protected CrafterBlockEntity createOnClient(FriendlyByteBuf extraData) {
        BlockPos readBlockPos = extraData.readBlockPos();
        CompoundTag readNbt = extraData.readNbt();
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null || readNbt == null) {
            return null;
        }

        BlockEntity blockEntity = world.getBlockEntity(readBlockPos);
        if (blockEntity instanceof CrafterBlockEntity crafterBlockEntity) {
            crafterBlockEntity.readClient(readNbt);
            return crafterBlockEntity;
        }
        return null;
    }

    @Override
    protected void addSlots() {
        this.contentHolder.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            this.addSlot(new InfoSlot(itemHandler, CRAFT_RESULT_SLOT, 222, 57));

            this.addSlots(itemHandler, SlotItemHandler::new, CRAFT_SLOT_START, 186, -2, 3, 3);
            this.addSlots(itemHandler, SlotItemHandler::new, CONTAINER_START, 78, 80, 2, 9);
            this.addSlots(itemHandler, ResultSlot::new, RESULT_SLOT_START, 38, 80, 2, 2);
        });
        this.addPlayerSlots(58, 167);
    }

    private void addSlots(IItemHandler itemHandler, SlotItemHandlerFactory factory, int index, int x, int y, int row, int col) {
        for (int iRow = 0; iRow < row; ++iRow)
            for (int iCol = 0; iCol < col; ++iCol)
                this.addSlot(factory.on(itemHandler, index + iCol + iRow * col, x + iCol * 18, y + iRow * 18));
    }


    @Override
    protected void initAndReadInventory(CrafterBlockEntity contentHolder) {
    }

    @Override
    protected void saveData(CrafterBlockEntity contentHolder) {
    }

    @Override
    public void clicked(int slotId, int dragType, @NotNull ClickType clickTypeIn, @NotNull Player player) {
        if (slotId < CRAFT_SLOT_START || slotId >= CONTAINER_START) {
            super.clicked(slotId, dragType, clickTypeIn, player);
            return;
        }
        if (clickTypeIn == ClickType.THROW)
            return;

        ItemStack held = this.getCarried();
        if (clickTypeIn == ClickType.CLONE) {
            if (player.isCreative() && held.isEmpty()) {
                ItemStack stackInSlot = this.contentHolder.inventory.getStackInSlot(slotId)
                        .copy();
                stackInSlot.setCount(stackInSlot.getMaxStackSize());
                this.setCarried(stackInSlot);
                return;
            }
            return;
        }

        ItemStack insert;
        if (held.isEmpty()) {
            insert = ItemStack.EMPTY;
        } else {
            insert = held.copy();
            insert.setCount(1);
        }
        this.contentHolder.inventory.setStackInSlot(slotId, insert);
        this.getSlot(slotId).setChanged();

        this.contentHolder.updateWorkInventory();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        if (index < CONTAINER_START) {
            return itemStack;
        }

        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack item = slot.getItem();
            itemStack = item.copy();

            if (index < PLAYER_SLOT_START) {
                if (!this.moveItemStackTo(item, PLAYER_SLOT_START, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(item, CONTAINER_START, RESULT_SLOT_START, false)) {
                return ItemStack.EMPTY;
            }

            if (item.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

        }
        return itemStack;
    }

    @Override
    public boolean canDragTo(Slot pSlot) {
        return super.canDragTo(pSlot);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return pSlot.container == this.playerInventory || pSlot.index >= CONTAINER_START;
    }

    public void transferRecipe(NonNullList<ItemStack> stacks) {
        if (stacks.isEmpty()) {
            return;
        }

        this.contentHolder.inventory.setStackInSlot(CRAFT_RESULT_SLOT, stacks.get(0));
        for (int i = 1; i < stacks.size(); i++) {
            this.contentHolder.inventory.setStackInSlot(CRAFT_SLOT_START + i - 1, stacks.get(i));
        }
        this.contentHolder.updateWorkInventory();
        this.contentHolder.setChanged();
    }
}
