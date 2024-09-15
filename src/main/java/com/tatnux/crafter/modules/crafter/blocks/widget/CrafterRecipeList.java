package com.tatnux.crafter.modules.crafter.blocks.widget;

import com.tatnux.crafter.modules.crafter.blocks.CrafterScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;

public class CrafterRecipeList extends ObjectSelectionList<CrafterRecipeList.RecipeEntry> {

    private final CrafterScreen parent;
    private final int listWidth;

    public CrafterRecipeList(CrafterScreen parent, int listWidth, int listHeight, int top, int bottom, int p_94447_) {
        super(parent.getMinecraft(), listWidth, listHeight, top, bottom, p_94447_);
        this.parent = parent;
        this.listWidth = listWidth;
    }

    public void refreshList() {
        this.clearEntries();
        this.addEntry(new RecipeEntry());
    }

    public class RecipeEntry extends ObjectSelectionList.Entry<RecipeEntry> {

        @Override
        public @NotNull Component getNarration() {
            return Component.literal("Entry");
        }

        @Override
        public void render(GuiGraphics guiGraphics, int pIndex, int top, int left, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick) {
            Component name = Component.literal("Entry");
            Font font = CrafterRecipeList.this.parent.getFontRenderer();
            guiGraphics.drawString(font, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(name, CrafterRecipeList.this.listWidth))), left + 3, top + 2, 0xFFFFFF, false);
        }
    }

}
