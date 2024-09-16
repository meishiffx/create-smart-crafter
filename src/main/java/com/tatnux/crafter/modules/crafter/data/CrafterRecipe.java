package com.tatnux.crafter.modules.crafter.data;

import lombok.Data;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
public class CrafterRecipe {

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

}
