package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.tatnux.crafter.lib.list.NonNullListFactory;
import com.tatnux.crafter.modules.crafter.CrafterModule;
import com.tatnux.crafter.modules.crafter.data.CrafterRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.tatnux.crafter.modules.crafter.blocks.CrafterMenu.CRAFT_RESULT_SLOT;
import static com.tatnux.crafter.modules.crafter.blocks.CrafterMenu.CRAFT_SLOT_START;

public class CrafterBlockEntity extends SmartBlockEntity implements MenuProvider {

    public final CrafterInventory inventory;
    private final LazyOptional<IItemHandler> inventoryProvider;

    public ContainerData dataAccess;
    public final NonNullList<CrafterRecipe> recipes;
    public int selected = 0;

    public CrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = new CrafterInventory(this);
        this.inventoryProvider = LazyOptional.of(() -> this.inventory);

        this.recipes = NonNullListFactory.fill(9, CrafterRecipe::new);

        this.dataAccess = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> CrafterBlockEntity.this.selected;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int i1) {
                switch (i) {
                    case 0 -> CrafterBlockEntity.this.select(i1);
                }
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
    }

    public void select(int index) {
        if (this.selected == index) {
            return;
        }
        this.selected = index;
        CrafterRecipe crafterRecipe = this.recipes.get(index);
        this.inventory.setStackInSlot(CRAFT_RESULT_SLOT, crafterRecipe.getOutput());
        for (int i = 0; i < crafterRecipe.getItems().size(); i++) {
            this.inventory.setStackInSlot(CRAFT_SLOT_START + i, crafterRecipe.getItems().get(i));
        }

    }

    public void saveRecipe(CraftingRecipe recipe, List<ItemStack> items, ItemStack output) {
        CrafterRecipe crafterRecipe = this.recipes.get(this.dataAccess.get(0));

        crafterRecipe.setCraftingGrid(items, output);
        crafterRecipe.setRecipe(recipe);

        this.inventory.setStackInSlot(CRAFT_RESULT_SLOT, output);
    }

    public void clearRecipe(List<ItemStack> items) {
        CrafterRecipe crafterRecipe = this.recipes.get(this.dataAccess.get(0));

        crafterRecipe.setCraftingGrid(items, ItemStack.EMPTY);
        crafterRecipe.setRecipe(null);

        this.inventory.setStackInSlot(CRAFT_RESULT_SLOT, ItemStack.EMPTY);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (this.isItemHandlerCap(cap)) {
            return this.inventoryProvider.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return CrafterModule.CRAFTER.get().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return CrafterMenu.create(id, inv, this);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("inventory", this.inventory.serializeNBT());
    }

    @Override
    public void writeSafe(CompoundTag tag) {
        super.writeSafe(tag);
//        tag.put("inventory", this.inventory.serializeNBT());
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
    }

    @Override
    public void saveToItem(ItemStack pStack) {
//        pStack.addTagElement("inventory", inventory.serializeNBT());
        super.saveToItem(pStack);
    }
}
