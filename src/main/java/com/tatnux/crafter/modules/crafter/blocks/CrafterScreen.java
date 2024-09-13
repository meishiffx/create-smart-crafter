package com.tatnux.crafter.modules.crafter.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.tatnux.crafter.modules.common.gui.GuiTexture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CrafterScreen extends AbstractSimiContainerScreen<CrafterMenu> {

    protected static final GuiTexture BG = GuiTexture.CRAFTER;
    protected static final AllGuiTextures PLAYER = AllGuiTextures.PLAYER_INVENTORY;


    public CrafterScreen(CrafterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        init();
    }

    @Override
    protected void init() {
        setWindowSize(30 + BG.width, BG.height + PLAYER.height - 24);
        setWindowOffset(-11, 0);
        super.init();

        IconButton confirmButton = new IconButton(leftPos + 30 + BG.width - 33, topPos + BG.height - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> {
            minecraft.player.closeContainer();
        });
        addRenderableWidget(confirmButton);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int x = leftPos + imageWidth - BG.width;
        int y = topPos;

        BG.render(graphics, x, y);
        graphics.drawString(font, title, x + 15, y + 4, 0x592424, false);

        int invX = leftPos;
        int invY = topPos + imageHeight - PLAYER.height;
        renderPlayerInventory(graphics, invX, invY);
    }
}
