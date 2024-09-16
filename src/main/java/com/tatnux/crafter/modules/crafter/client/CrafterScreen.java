package com.tatnux.crafter.modules.crafter.client;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.tatnux.crafter.jei.PacketSendRecipe;
import com.tatnux.crafter.lib.gui.GuiTexture;
import com.tatnux.crafter.modules.crafter.blocks.CrafterMenu;
import com.tatnux.crafter.modules.crafter.client.widget.RecipeList;
import com.tatnux.crafter.modules.crafter.data.CrafterRecipe;
import com.tatnux.crafter.modules.network.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public class CrafterScreen extends AbstractSimiContainerScreen<CrafterMenu> {

    protected static final GuiTexture BG = GuiTexture.CRAFTER;
    protected static final AllGuiTextures PLAYER = AllGuiTextures.PLAYER_INVENTORY;

    public final NonNullList<CrafterRecipe> recipes = NonNullList.withSize(9, new CrafterRecipe());

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

        this.addRenderableWidget(new IconButton(
                this.leftPos + 185,
                this.topPos + 56,
                AllIcons.I_TRASH)
                .withCallback(() -> {
                    NetworkHandler.sendRecipeToServer(PacketSendRecipe.create(NonNullList.withSize(10, ItemStack.EMPTY)));
                }));


        RecipeList recipeList = new RecipeList(this, this.leftPos + 38, this.topPos - 2, 110, 75);
        this.addRenderableWidget(recipeList);
        recipeList.init();
    }

    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(@NotNull T pWidget) {
        return super.addRenderableWidget(pWidget);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int x = this.leftPos + this.imageWidth - BG.width;
        int y = this.topPos - 23;

        BG.render(graphics, x, y);
        graphics.drawString(this.font, this.title, x + 15, y + 4, Color.decode("#BEBEBE").getRGB(), false);

        int invX = this.getLeftOfCentered(PLAYER_INVENTORY.width);
        int invY = this.topPos + this.imageHeight - PLAYER.height;
        this.renderPlayerInventory(graphics, invX, invY);
    }

}
