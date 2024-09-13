package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import com.tatnux.crafter.modules.crafter.CrafterModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class CrafterMenu extends MenuBase<CrafterBlockEntity> {

    public static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CONTAINER_START = 10;

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
        BlockEntity blockEntity = world.getBlockEntity(readBlockPos);
        if (blockEntity instanceof CrafterBlockEntity crafterBlockEntity) {
            crafterBlockEntity.readClient(readNbt);
            return crafterBlockEntity;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(CrafterBlockEntity contentHolder) {

    }

    @Override
    protected void addSlots() {
        this.contentHolder.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            this.addSlot(new SlotItemHandler(itemHandler, RESULT_SLOT, 183, 78));

            for (int row = 0; row < 3; ++row)
                for (int col = 0; col < 3; ++col)
                    this.addSlot(new SlotItemHandler(itemHandler, CRAFT_SLOT_START + col + row * 3, 147 + col * 18, 21 + row * 18));

            for (int row = 0; row < 2; ++row)
                for (int col = 0; col < 9; ++col)
                    this.addSlot(new SlotItemHandler(itemHandler, CONTAINER_START + col + row * 9, 40 + col * 18, 101 + row * 18));
        });


        this.addPlayerSlots(8, 165);
    }

    @Override
    protected void saveData(CrafterBlockEntity contentHolder) {

    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}
