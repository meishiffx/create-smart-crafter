package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.tatnux.crafter.modules.common.gui.GuiTexture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;

public class CrafterScreen extends AbstractSimiContainerScreen<CrafterMenu> {

    protected static final GuiTexture BG = GuiTexture.CRAFTER;
    protected static final AllGuiTextures PLAYER = AllGuiTextures.PLAYER_INVENTORY;

    public CrafterScreen(CrafterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.init();
    }

    @Override
    protected void init() {
        this.setWindowSize(30 + BG.width, BG.height + PLAYER.height - 24);
        this.setWindowOffset(-11, 0);
        super.init();

        IconButton confirmButton = new IconButton(this.leftPos + 30 + BG.width - 33, this.topPos + BG.height - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> {
            this.minecraft.player.closeContainer();
        });
        this.addRenderableWidget(confirmButton);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int x = this.leftPos + this.imageWidth - BG.width;
        int y = this.topPos;

        BG.render(graphics, x, y);
        graphics.drawString(this.font, this.title, x + 15, y + 4, Color.decode("#BEBEBE").getRGB(), false);

        int invX = this.leftPos;
        int invY = this.topPos + this.imageHeight - PLAYER.height;
        this.renderPlayerInventory(graphics, invX, invY);
    }
}
