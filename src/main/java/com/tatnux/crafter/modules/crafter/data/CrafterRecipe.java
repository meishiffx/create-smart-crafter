package com.tatnux.crafter.modules.crafter.data;

import lombok.Data;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;

@Data
public class CrafterRecipe {

    private Recipe<?> recipe;
    private ItemStack output = ItemStack.EMPTY;

    public static Optional<CraftingRecipe> findRecipe(Level level, CraftingContainer container) {
        return level.getRecipeManager().getRecipes().stream()
                .filter(Objects::nonNull)
                .filter(recipe -> recipe instanceof CraftingRecipe craftingRecipe && craftingRecipe.matches(container, level))
                .map(recipe1 -> ((CraftingRecipe) recipe1))
                .findFirst();
    }

}
