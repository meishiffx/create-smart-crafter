package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.tatnux.crafter.modules.common.gui.GuiTexture;
import com.tatnux.crafter.modules.crafter.blocks.widget.CrafterRecipeList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public class CrafterScreen extends AbstractSimiContainerScreen<CrafterMenu> {

    protected static final GuiTexture BG = GuiTexture.CRAFTER;
    protected static final AllGuiTextures PLAYER = AllGuiTextures.PLAYER_INVENTORY;

    private CrafterRecipeList recipeList;

    public CrafterScreen(CrafterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        this.setWindowSize(30 + BG.width, BG.height + PLAYER.height - 24);
        this.setWindowOffset(-11, 0);
        super.init();

        this.addRenderableWidget(new IconButton(
                this.leftPos + 30 + BG.width - 33,
                this.topPos + BG.height - 47,
                AllIcons.I_CONFIRM)
                .withCallback(() -> {
                    this.minecraft.player.closeContainer();
                }));

        this.recipeList = new CrafterRecipeList(
                this,
                200,
                50,
                80,
                50,
                50
        );
        this.recipeList.setLeftPos(this.leftPos);
        this.recipeList.refreshList();

        this.addRenderableWidget(this.recipeList);

    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.recipeList.render(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int x = this.leftPos + this.imageWidth - BG.width;
        int y = this.topPos - 23;

        BG.render(graphics, x, y);
        graphics.drawString(this.font, this.title, x + 15, y + 4, Color.decode("#BEBEBE").getRGB(), false);

        int invX = this.getLeftOfCentered(PLAYER_INVENTORY.width);
        int invY = this.topPos + this.imageHeight - PLAYER.height;
        this.renderPlayerInventory(graphics, invX, invY);
    }

    public Font getFontRenderer() {
        return this.font;
    }

}
