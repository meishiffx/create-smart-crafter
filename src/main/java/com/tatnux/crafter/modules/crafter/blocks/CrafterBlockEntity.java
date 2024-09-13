package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.tatnux.crafter.modules.crafter.CrafterModule;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrafterBlockEntity extends SmartBlockEntity implements MenuProvider {

    public final CrafterInventory inventory;
    private final LazyOptional<IItemHandler> inventoryProvider;

    public CrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = new CrafterInventory(this);
        this.inventoryProvider = LazyOptional.of(() -> this.inventory);
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
}
