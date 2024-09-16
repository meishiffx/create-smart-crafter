package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.tatnux.crafter.lib.list.NonNullListFactory;
import com.tatnux.crafter.lib.menu.InventoryTools;
import com.tatnux.crafter.lib.menu.UndoableItemHandler;
import com.tatnux.crafter.modules.crafter.CrafterModule;
import com.tatnux.crafter.modules.crafter.blocks.inventory.WorkingCraftingInventory;
import com.tatnux.crafter.modules.crafter.data.CraftMode;
import com.tatnux.crafter.modules.crafter.data.CrafterRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.tatnux.crafter.modules.crafter.blocks.CrafterMenu.CRAFT_RESULT_SLOT;
import static com.tatnux.crafter.modules.crafter.blocks.CrafterMenu.CRAFT_SLOT_START;
import static com.tatnux.crafter.modules.crafter.data.CraftMode.EXTC;
import static com.tatnux.crafter.modules.crafter.data.CraftMode.INT;

public class CrafterBlockEntity extends SmartBlockEntity implements MenuProvider {

    public final CrafterInventory inventory;
    private final LazyOptional<IItemHandler> inventoryProvider;

    public final NonNullList<CrafterRecipe> recipes;
    public byte selected = 0;

    public boolean keepMode = false;
    private final CraftingContainer workInventory = new WorkingCraftingInventory();

    public CrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = new CrafterInventory(this);
        this.inventoryProvider = LazyOptional.of(() -> this.inventory);

        this.recipes = NonNullListFactory.fill(9, CrafterRecipe::new);
    }

    public void select(byte index) {
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

    public CrafterRecipe getSelectedRecipe() {
        return this.recipes.get(this.selected);
    }

    public void setCraftMode(CraftMode mode) {
        this.getSelectedRecipe().setCraftMode(mode);
        this.notifyUpdate();
    }

    public void setKeepMode(boolean keep) {
        this.keepMode = keep;
        this.notifyUpdate();
    }

    public void saveRecipe(CraftingRecipe recipe, List<ItemStack> items, ItemStack output) {
        this.getSelectedRecipe().setCraftingGrid(items, output);
        this.getSelectedRecipe().setRecipe(recipe);

        this.inventory.setStackInSlot(CRAFT_RESULT_SLOT, output);
    }

    public void clearRecipe(List<ItemStack> items) {
        this.getSelectedRecipe().setCraftingGrid(items, ItemStack.EMPTY);
        this.getSelectedRecipe().setRecipe(null);

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
    public void invalidateCaps() {
        super.invalidateCaps();
        this.inventoryProvider.invalidate();
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
    public void tick() {
        if (this.level.isClientSide()) {
            return;
        }

        this.craftOneCycle();
    }

    private boolean craftOneCycle() {
        return this.recipes.stream().anyMatch(this::craftRecipe);
    }

    private boolean craftRecipe(CrafterRecipe recipe) {
        Optional<CraftingRecipe> optionalCraftingRecipe = recipe.getCachedRecipe(this.level);
        if (optionalCraftingRecipe.isEmpty()) {
            return false;
        }

        CraftingRecipe craftingRecipe = optionalCraftingRecipe.get();

        UndoableItemHandler undoHandler = new UndoableItemHandler(this.inventory);
        if (!this.testAndConsume(recipe, undoHandler)) {
            undoHandler.restore();
            return false;
        }

        ItemStack result = craftingRecipe.assemble(this.workInventory, this.level.registryAccess());

        CraftMode mode = recipe.getCraftMode();
        if (!result.isEmpty() && this.placeResult(mode, undoHandler, result)) {
            List<ItemStack> remaining = craftingRecipe.getRemainingItems(this.workInventory);
            CraftMode remainingMode = mode == EXTC ? INT : mode;
            for (ItemStack s : remaining) {
                if (!s.isEmpty()) {
                    if (!this.placeResult(remainingMode, undoHandler, s)) {
                        // Not enough room.
                        undoHandler.restore();
                        return false;
                    }
                }
            }
            return true;
        } else {
            // We don't have place. Undo the operation.
            undoHandler.restore();
            return false;
        }

    }

    private boolean testAndConsume(CrafterRecipe crafterRecipe, UndoableItemHandler undoHandler) {
        int keep = this.keepMode ? 1 : 0;
        for (int i = 0; i < this.workInventory.getContainerSize(); i++) {
            this.workInventory.setItem(i, ItemStack.EMPTY);
        }

        CraftingRecipe recipe = crafterRecipe.getRecipe();
        int w = 3;
        int h = 3;
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            w = shapedRecipe.getRecipeWidth();
            h = shapedRecipe.getRecipeHeight();
        }

        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int index = y * w + x;
                if (index < ingredients.size()) {
                    Ingredient ingredient = ingredients.get(index);
                    if (ingredient != Ingredient.EMPTY) {
                        for (int j = 0; j < CrafterMenu.CONTAINER_SIZE; j++) {
                            int slotIdx = CrafterMenu.CONTAINER_START + j;
                            ItemStack input = undoHandler.getStackInSlot(slotIdx);
                            if (!input.isEmpty() && input.getCount() > keep) {
                                if (ingredient.test(input)) {
                                    undoHandler.remember(slotIdx);
                                    ItemStack copy = input.split(1);
                                    this.workInventory.setItem(y * 3 + x, copy);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return recipe.matches(this.workInventory, this.level);
    }

    private boolean placeResult(CraftMode mode, IItemHandlerModifiable undoHandler, ItemStack result) {
        int start;
        int stop;
        if (mode == INT) {
            start = CrafterMenu.CONTAINER_START;
            stop = CrafterMenu.CONTAINER_START + CrafterMenu.CONTAINER_SIZE;
        } else {
            start = CrafterMenu.RESULT_SLOT_START;
            stop = CrafterMenu.RESULT_SLOT_START + CrafterMenu.RESULT_SLOT_SIZE;
        }
        ItemStack remaining = InventoryTools.insertItemRanged(undoHandler, result, start, stop, true);
        if (remaining.isEmpty()) {
            InventoryTools.insertItemRanged(undoHandler, result, start, stop, false);
            return true;
        }
        return false;
    }

    public void updateWorkInventory() {
        if (this.getLevel().isClientSide) {
            return;
        }
        for (int i = 0; i < 9; i++) {
            this.workInventory.setItem(i, this.inventory.getStackInSlot(i + CRAFT_SLOT_START));
        }
        CrafterRecipe.findRecipe(this.getLevel(), this.workInventory).ifPresentOrElse(recipe -> {
            ItemStack result = recipe.assemble(this.workInventory, this.getLevel().registryAccess());
            this.saveRecipe(recipe, this.workInventory.getItems(), result);
        }, () -> this.clearRecipe(this.workInventory.getItems()));
    }


    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("Inventory", this.inventory.serializeNBT());
        tag.putByte("SelectedRecipe", this.selected);

        // Recipes
        ListTag recipes = new ListTag();
        for (CrafterRecipe crafterRecipe : this.recipes) {
            CompoundTag recipeTag = new CompoundTag();
            recipeTag.put("Recipe", crafterRecipe.serializeNBT());
            recipes.add(recipeTag);
        }
        tag.put("Recipes", recipes);
        tag.putBoolean("KeepMode", this.keepMode);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.inventory.deserializeNBT(tag.getCompound("Inventory"));
        this.selected = tag.getByte("SelectedRecipe");

        // Recipes
        ListTag recipes = tag.getList("Recipes", Tag.TAG_COMPOUND);
        for (int i = 0; i < recipes.size(); i++) {
            CompoundTag recipeTag = recipes.getCompound(i);
            this.recipes.get(i).deserializeNBT(recipeTag.getCompound("Recipe"));
        }
        this.keepMode = tag.getBoolean("KeepMode");
    }
}
