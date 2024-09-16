package com.tatnux.crafter.modules.crafter.data;

import lombok.Data;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
public class CrafterRecipe implements INBTSerializable<CompoundTag> {

    private Recipe<?> recipe;
    private ItemStack output = ItemStack.EMPTY;
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

    public void setCraftingGrid(List<ItemStack> items, ItemStack output) {
        if (items.size() > this.items.size()) {
            throw new IllegalArgumentException("The size of the items array exceeds the size of the recipe items list.");
        }
        for (int i = 0; i < items.size(); i++) {
            this.items.set(i, items.get(i));
        }
        this.output = output;
    }

    public static Optional<CraftingRecipe> findRecipe(Level level, CraftingContainer container) {
        return level.getRecipeManager().getRecipes().stream()
                .filter(Objects::nonNull)
                .filter(recipe -> recipe instanceof CraftingRecipe craftingRecipe && craftingRecipe.matches(container, level))
                .map(recipe1 -> ((CraftingRecipe) recipe1))
                .findFirst();
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag listTag = new ListTag();
        for (int i = 0; i < this.items.size(); i++) {
            ItemStack itemStack = this.items.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                itemStack.save(itemTag);
                listTag.add(itemTag);
            }
        }
        CompoundTag tag = new CompoundTag();
        tag.put("Items", listTag);
        if (!this.output.isEmpty()) {
            tag.put("Output", this.output.serializeNBT());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag listTag = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag itemTags = listTag.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < this.items.size()) {
                this.items.set(slot, ItemStack.of(itemTags));
            }
        }
        this.output = nbt.contains("Output") ? ItemStack.of(nbt.getCompound("Output")) : ItemStack.EMPTY;
    }
}
