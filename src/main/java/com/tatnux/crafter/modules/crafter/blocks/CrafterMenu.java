package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import com.tatnux.crafter.modules.crafter.CrafterModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CrafterMenu extends GhostItemMenu<CrafterBlockEntity> {

    public static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CONTAINER_START = 10;

    public CrafterMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public CrafterMenu(MenuType<?> type, int id, Inventory inv, CrafterBlockEntity be) {
        super(type, id, inv, be);

    }

    @Override
    protected ItemStackHandler createGhostInventory() {
        return new ItemStackHandler(10);
    }

    @Override
    protected boolean allowRepeats() {
        return false;
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
        this.addPlayerSlots(8, 165);

        this.contentHolder.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            this.addSlot(new SlotItemHandler(this.ghostInventory, RESULT_SLOT, 183, 78));

            for (int row = 0; row < 3; ++row)
                for (int col = 0; col < 3; ++col)
                    this.addSlot(new SlotItemHandler(this.ghostInventory, CRAFT_SLOT_START + col + row * 3, 147 + col * 18, 21 + row * 18));

            for (int row = 0; row < 2; ++row)
                for (int col = 0; col < 9; ++col)
                    this.addSlot(new SlotItemHandler(itemHandler, CONTAINER_START + col + row * 9, 40 + col * 18, 101 + row * 18));
        });
    }

    @Override
    protected void saveData(CrafterBlockEntity contentHolder) {

    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    public void transferRecipe(NonNullList<ItemStack> stacks) {
        if (stacks.isEmpty()) {
            return;
        }

        this.contentHolder.inventory.setStackInSlot(RESULT_SLOT, stacks.get(0));

        for (int i = 1; i < stacks.size(); i++) {
            this.contentHolder.inventory.setStackInSlot(CRAFT_SLOT_START + i - 1, stacks.get(i));
        }
        this.broadcastChanges();
    }
}
