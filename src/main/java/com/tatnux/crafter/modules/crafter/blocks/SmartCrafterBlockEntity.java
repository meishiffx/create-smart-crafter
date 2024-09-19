package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.tatnux.crafter.config.Config;
import com.tatnux.crafter.lib.list.NonNullListFactory;
import com.tatnux.crafter.lib.menu.InventoryTools;
import com.tatnux.crafter.lib.menu.UndoableItemHandler;
import com.tatnux.crafter.modules.crafter.SmartCrafterModule;
import com.tatnux.crafter.modules.crafter.blocks.inventory.AutomationInventory;
import com.tatnux.crafter.modules.crafter.blocks.inventory.CrafterInventory;
import com.tatnux.crafter.modules.crafter.blocks.inventory.WorkingCraftingInventory;
import com.tatnux.crafter.modules.crafter.data.CraftMode;
import com.tatnux.crafter.modules.crafter.data.CrafterRecipe;
import com.tatnux.crafter.modules.crafter.data.GhostSlots;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Block;
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

import static com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu.*;
import static com.tatnux.crafter.modules.crafter.data.CraftMode.EXTC;
import static com.tatnux.crafter.modules.crafter.data.CraftMode.INT;

public class SmartCrafterBlockEntity extends KineticBlockEntity implements MenuProvider {

    public final CrafterInventory inventory;
    private final AutomationInventory automationInventory;
    private final LazyOptional<IItemHandler> inventoryProvider;

    public final NonNullList<CrafterRecipe> recipes;
    public byte selected = 0;

    public boolean keepMode = false;
    public GhostSlots ghostSlots;
    private final CraftingContainer workInventory = new WorkingCraftingInventory();
    private boolean noRecipeWork = false;
    private float progress = -1;
    private boolean processing = false;

    public SmartCrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = new CrafterInventory(this);
        this.automationInventory = new AutomationInventory(this.inventory);
        this.inventoryProvider = LazyOptional.of(() -> this.automationInventory);

        this.ghostSlots = new GhostSlots();

        this.recipes = NonNullListFactory.fill(9, CrafterRecipe::new);
    }

    /// From Client
    public void select(byte index) {
        // Already selected, return
        if (this.selected == index) {
            return;
        }

        // The recipe is already cached so we just have to update the inventory
        this.selected = index;
        CrafterRecipe crafterRecipe = this.getSelectedRecipe();
        this.inventory.setStackInSlot(CRAFT_RESULT_SLOT, crafterRecipe.getOutput());
        for (int i = 0; i < crafterRecipe.getItems().size(); i++) {
            this.inventory.setStackInSlot(CRAFT_SLOT_START + i, crafterRecipe.getItems().get(i));
        }
    }

    public void reset(byte index) {
        if (this.selected != index) {
            // Illegal State
            return;
        }

        this.transferRecipe(NonNullList.withSize(10, ItemStack.EMPTY));
        this.retryRecipe();
    }

    public void retryRecipe() {
        this.noRecipeWork = false;
    }

    // From Client
    public void setCraftMode(CraftMode mode) {
        this.getSelectedRecipe().setCraftMode(mode);
        this.notifyUpdate();
    }

    // From Client
    public void setKeepMode(boolean keep) {
        this.keepMode = keep;
        this.notifyUpdate();
    }

    public CrafterRecipe getSelectedRecipe() {
        return this.recipes.get(this.selected);
    }

    public void saveRecipe(CraftingRecipe recipe, List<ItemStack> items, ItemStack output) {
        this.getSelectedRecipe().setCraftingGrid(items, output);
        this.getSelectedRecipe().setRecipe(recipe);

        this.inventory.setStackInSlot(CRAFT_RESULT_SLOT, output);
        this.retryRecipe();
    }

    public void updateGhostItems(boolean reset) {
        if (reset) {
            this.ghostSlots.clear();
        } else {
            for (byte i = CONTAINER_START; i < RESULT_SLOT_START + RESULT_SLOT_SIZE; i++) {
                ItemStack itemStack = this.inventory.getStackInSlot(i);
                if (!itemStack.isEmpty()) {
                    this.ghostSlots.addSlot(itemStack.copyWithCount(1), i);
                }
            }
        }
        this.notifyUpdate();
        this.retryRecipe();
    }

    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot >= CONTAINER_START && slot < RESULT_SLOT_START + RESULT_SLOT_SIZE) {
            return this.ghostSlots.mayPlace((byte) slot, stack);
        }
        return true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (this.isItemHandlerCap(cap)) {
            return this.inventoryProvider.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.inventoryProvider.invalidate();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return SmartCrafterModule.SMART_CRAFTER.get().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return SmartCrafterMenu.create(id, inv, this);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            return;
        }

        if (this.noRecipeWork || this.getSpeed() == 0) {
            return;
        }

        if (this.processing) {
            if (this.progress <= 0) {
                if (!this.craftOneCycle()) {
                    this.noRecipeWork = true;
                }
                this.processing = false;
            } else {
                this.progress -= Mth.clamp(Math.abs(this.getSpeed()) / Config.common().speedRatio.get(), 1, 128);
            }
        } else {
            this.progress = Config.common().crafterProgressNeeded.get();
            this.processing = true;
        }
    }

    private boolean craftOneCycle() {
        boolean craftedAtLeastOneThing = false;
        for (CrafterRecipe craftingRecipe : this.recipes) {
            if (this.craftRecipe(craftingRecipe)) {
                craftedAtLeastOneThing = true;
            }
        }
        return craftedAtLeastOneThing;
    }

    private boolean craftRecipe(CrafterRecipe recipe) {
        Optional<CraftingRecipe> optionalCraftingRecipe = recipe.getCachedRecipe(this.level);
        // If the current recipe has no recipe, return
        if (optionalCraftingRecipe.isEmpty()) {
            return false;
        }

        UndoableItemHandler undoHandler = new UndoableItemHandler(this.inventory);
        if (!this.testAndConsume(recipe, undoHandler)) {
            undoHandler.restore();
            return false;
        }

        CraftingRecipe craftingRecipe = optionalCraftingRecipe.get();
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
        int keep = this.keepMode ? 1 : 0; //The number of item that will remain in the container slots
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
                        for (int j = 0; j < SmartCrafterMenu.CONTAINER_SIZE; j++) {
                            int slotIdx = SmartCrafterMenu.CONTAINER_START + j;
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
        // Set the target container slots depending on the current mode
        if (mode == INT) {
            start = SmartCrafterMenu.CONTAINER_START;
            stop = SmartCrafterMenu.CONTAINER_START + SmartCrafterMenu.CONTAINER_SIZE;
        } else {
            start = SmartCrafterMenu.RESULT_SLOT_START;
            stop = SmartCrafterMenu.RESULT_SLOT_START + SmartCrafterMenu.RESULT_SLOT_SIZE;
        }
        // Test if the operation is ok
        ItemStack remaining = InventoryTools.insertItemRanged(undoHandler, result, start, stop, true);
        if (remaining.isEmpty()) {
            // Same method but for real (not simulated)
            InventoryTools.insertItemRanged(undoHandler, result, start, stop, false);
            return true;
        }
        // Here we would have items left
        return false;
    }

    public void updateWorkInventory() {
        if (this.getLevel().isClientSide) {
            return;
        }
        // Update the work inventory and try to find a recipe
        for (int i = 0; i < 9; i++) {
            this.workInventory.setItem(i, this.inventory.getStackInSlot(i + CRAFT_SLOT_START));
        }
        CrafterRecipe.findRecipe(this.getLevel(), this.workInventory).ifPresentOrElse(recipe -> {
            ItemStack result = recipe.assemble(this.workInventory, this.getLevel().registryAccess());
            this.saveRecipe(recipe, this.workInventory.getItems(), result);
        }, () -> this.saveRecipe(null, this.workInventory.getItems(), ItemStack.EMPTY)); // If no recipe was found, reset the recipe to empty
    }

    public void transferRecipe(NonNullList<ItemStack> stacks) {
        if (stacks.isEmpty()) {
            return;
        }

        // Update the inventory
        this.inventory.setStackInSlot(CRAFT_RESULT_SLOT, stacks.get(0));
        for (int i = 1; i < stacks.size(); i++) {
            this.inventory.setStackInSlot(CRAFT_SLOT_START + i - 1, stacks.get(i));
        }

        // Test recipe and save
        this.updateWorkInventory();
        this.setChanged();
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("Inventory", this.inventory.serializeNBT()); // Inventory
        tag.putByte("SelectedRecipe", this.selected); // SelectedRecipe

        // Create the recipe list
        ListTag recipes = new ListTag();
        for (CrafterRecipe crafterRecipe : this.recipes) {
            CompoundTag recipeTag = new CompoundTag();
            recipeTag.put("Recipe", crafterRecipe.serializeNBT());
            recipes.add(recipeTag);
        }
        tag.put("Recipes", recipes); // Recipes
        tag.putBoolean("KeepMode", this.keepMode); // KeepMode
        tag.put("GhostSlots", this.ghostSlots.serializeNBT()); // GhostSlots
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.inventory.deserializeNBT(tag.getCompound("Inventory")); // Inventory
        this.selected = tag.getByte("SelectedRecipe"); // SelectedRecipe

        // Retrieve the recipes list
        ListTag recipes = tag.getList("Recipes", Tag.TAG_COMPOUND); // Recipes
        for (int i = 0; i < recipes.size(); i++) {
            CompoundTag recipeTag = recipes.getCompound(i);
            this.recipes.get(i).deserializeNBT(recipeTag.getCompound("Recipe"));
        }
        this.keepMode = tag.getBoolean("KeepMode"); // KeepMode
        this.ghostSlots.deserializeNBT(tag.getList("GhostSlots", Tag.TAG_COMPOUND)); // GhostsSlots
    }
}
