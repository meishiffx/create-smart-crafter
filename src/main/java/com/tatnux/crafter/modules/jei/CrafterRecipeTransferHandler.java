package com.tatnux.crafter.modules.jei;

import com.tatnux.crafter.modules.crafter.CrafterModule;
import com.tatnux.crafter.modules.crafter.blocks.CrafterMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class CrafterRecipeTransferHandler implements IRecipeTransferHandler<CrafterMenu, CraftingRecipe> {

    public static void register(IRecipeTransferRegistration transferRegistry) {
        transferRegistry.addRecipeTransferHandler(new CrafterRecipeTransferHandler(), RecipeTypes.CRAFTING);
    }

    @Override
    @Nonnull
    public Class<CrafterMenu> getContainerClass() {
        return CrafterMenu.class;
    }

    @Override
    public @NotNull Optional<MenuType<CrafterMenu>> getMenuType() {
        return Optional.of(CrafterModule.CRAFTER_MENU.get());
    }

    @Override
    public @NotNull RecipeType<CraftingRecipe> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    @Override
    @Nullable
    public IRecipeTransferError transferRecipe(CrafterMenu container, @NotNull CraftingRecipe recipe, IRecipeSlotsView recipeLayout,
                                               @NotNull Player player, boolean maxTransfer, boolean doTransfer) {
        BlockEntity inventory = container.contentHolder;
        BlockPos pos = inventory.getBlockPos();
        List<IRecipeSlotView> slotViews = recipeLayout.getSlotViews();

        if (doTransfer) {
//            RFToolsUtilityJeiPlugin.transferRecipe(slotViews, pos);
        }

        return null;
    }
}
