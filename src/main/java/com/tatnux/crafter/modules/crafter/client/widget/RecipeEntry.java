package com.tatnux.crafter.modules.crafter.client.widget;

import com.simibubi.create.foundation.gui.widget.AbstractSimiWidget;
import com.tatnux.crafter.modules.crafter.client.CrafterScreen;
import com.tatnux.crafter.modules.crafter.data.CrafterRecipe;
import com.tatnux.crafter.modules.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class RecipeEntry extends AbstractSimiWidget {

    public static final int HOVER_COLOR = new Color(130, 143, 143).getRGB();

    public byte index;
    private final CrafterScreen parent;

    protected RecipeEntry(CrafterScreen parent, byte index, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.parent = parent;
        this.index = index;

    }

    @Override
    protected void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int color = -1;
        if (this.parent.getMenu().contentHolder.selected == this.index) {
            color = Color.BLUE.getRGB();
        } else if (this.isMouseOver(mouseX, mouseY)) {
            color = HOVER_COLOR;
        } else if (this.index % 2 == 0) {
            color = Color.GRAY.getRGB();
        }

        if (color != -1) {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color);
        }

        NonNullList<CrafterRecipe> recipes = this.parent.getMenu().contentHolder.recipes;
        if (recipes == null || this.index >= recipes.size()) {
            return;
        }

        CrafterRecipe recipe = recipes.get(this.index);
        ItemStack output = recipe.getOutput();
        graphics.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth() - 1, this.getY() + this.getHeight());

        if (!output.isEmpty()) {
            graphics.renderItem(output, this.getX(), this.getY());
        }

        graphics.drawString(Minecraft.getInstance().font,
                output.isEmpty() ? Component.literal("<empty>") : output.getHoverName(),
                this.getX() + 19,
                this.getY() + 4,
                0xFFFFFF,
                false);

        graphics.disableScissor();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        NetworkHandler.selectSlot(this.index);
    }
}
