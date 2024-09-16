package com.tatnux.crafter.modules.crafter.client;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.AbstractSimiWidget;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.tatnux.crafter.jei.PacketSendRecipe;
import com.tatnux.crafter.lib.gui.CrafterIconButton;
import com.tatnux.crafter.lib.gui.GuiTexture;
import com.tatnux.crafter.lib.gui.WidgetBox;
import com.tatnux.crafter.modules.crafter.blocks.CrafterMenu;
import com.tatnux.crafter.modules.crafter.client.widget.RecipeList;
import com.tatnux.crafter.modules.crafter.data.CraftMode;
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

    public static final int BOX_COLOR = new Color(50, 100, 181).getRGB();
    protected static final GuiTexture BG = GuiTexture.CRAFTER;
    protected static final AllGuiTextures PLAYER = AllGuiTextures.PLAYER_INVENTORY;

    private AbstractSimiWidget resetButton;

    public CrafterScreen(CrafterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        this.setWindowSize(30 + BG.width, BG.height + PLAYER.height - 24);
        this.setWindowOffset(-11, 0);
        super.init();

        int yFooter = this.topPos + BG.height - 47;

        this.addRenderableWidget(new IconButton(
                this.leftPos + 30 + BG.width - 33,
                yFooter,
                AllIcons.I_CONFIRM)
                .withCallback(() -> {
                    this.minecraft.player.closeContainer();
                }));

        this.resetButton = new IconButton(
                this.leftPos + 185,
                this.topPos + 56,
                AllIcons.I_TRASH)
                .withCallback(() -> {
                    NonNullList<ItemStack> items = NonNullList.withSize(10, ItemStack.EMPTY);
                    NetworkHandler.sendRecipeToServer(PacketSendRecipe.create(items));
                });
        this.addRenderableWidget(this.resetButton);
        this.addRenderableWidget(new WidgetBox(this.resetButton, BOX_COLOR));

        this.resetButton = new IconButton(
                this.leftPos + 185,
                this.topPos + 56,
                AllIcons.I_TRASH)
                .withCallback(() -> {
                    NonNullList<ItemStack> items = NonNullList.withSize(10, ItemStack.EMPTY);
                    NetworkHandler.sendRecipeToServer(PacketSendRecipe.create(items));
                });

        int buttonsX = this.leftPos + 40;

        AbstractSimiWidget extButton = new CrafterIconButton(
                buttonsX,
                yFooter,
                AllIcons.I_PRIORITY_VERY_LOW)
                .withDisabled(() -> this.menu.contentHolder.getSelectedRecipe().getCraftMode() == CraftMode.EXT)
                .withTooltip(Component.literal("Ext"))
                .withDescription(Component.literal("All items output will go in the result slots."))
                .withCallback(() -> this.updateCraftMode(CraftMode.EXT));

        AbstractSimiWidget extCButton = new CrafterIconButton(
                buttonsX + 18,
                yFooter,
                AllIcons.I_PRIORITY_LOW)
                .withDisabled(() -> this.menu.contentHolder.getSelectedRecipe().getCraftMode() == CraftMode.EXTC)
                .withTooltip(Component.literal("Secondary Int"))
                .withDescription(Component.literal("The primary result will go in the result slots"), Component.literal("and the secondary item in the ingredients slots."))
                .withCallback(() -> this.updateCraftMode(CraftMode.EXTC));

        AbstractSimiWidget intButton = new CrafterIconButton(
                buttonsX + 18 + 18,
                yFooter,
                AllIcons.I_ROTATE_CCW)
                .withDisabled(() -> this.menu.contentHolder.getSelectedRecipe().getCraftMode() == CraftMode.INT)
                .withTooltip(Component.literal("Int"))
                .withDescription(Component.literal("All items output will go back in the ingredients slots."))
                .withCallback(() -> this.updateCraftMode(CraftMode.INT));

        int keepGap = 18 * 4;

        AbstractSimiWidget keepButton = new CrafterIconButton(
                buttonsX + keepGap,
                yFooter,
                AllIcons.I_WHITELIST_OR)
                .withDisabled(() -> this.menu.contentHolder.keepMode)
                .withTooltip(Component.literal("Keep Mode"))
                .withDescription(Component.literal("1 item will remain in each ingredients slots."))
                .withCallback(() -> this.updateKeepMode(true));

        AbstractSimiWidget dontKeepButton = new CrafterIconButton(
                buttonsX + keepGap + 18,
                yFooter,
                AllIcons.I_WHITELIST_NOT)
                .withDisabled(() -> !this.menu.contentHolder.keepMode)
                .withTooltip(Component.literal("Out Mode"))
                .withDescription(Component.literal("No item is kept in the ingredients slots."))
                .withCallback(() -> this.updateKeepMode(false));

        this.addRenderableWidgets(extButton, extCButton, intButton, keepButton, dontKeepButton);


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

    private void updateCraftMode(CraftMode mode) {
        CrafterRecipe selectedRecipe = this.menu.contentHolder.getSelectedRecipe();
        if (selectedRecipe.getCraftMode() != mode) {
            NetworkHandler.selectCraftMode(mode);
        }
    }

    private void updateKeepMode(boolean keepMode) {
        if (this.menu.contentHolder.keepMode != keepMode) {
            NetworkHandler.setKeepMode(keepMode);
        }
    }

}
