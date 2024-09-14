package com.tatnux.crafter.modules.jei;

import com.tatnux.crafter.SimplyCrafter;
import com.tatnux.crafter.modules.crafter.CrafterModule;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class SimplyCrafterJeiPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(SimplyCrafter.MOD_ID, SimplyCrafter.MOD_ID);
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        CrafterRecipeTransferHandler.register(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(CrafterModule.CRAFTER), RecipeTypes.CRAFTING);
    }
}
