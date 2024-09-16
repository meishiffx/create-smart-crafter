package com.tatnux.crafter.lib.menu;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public interface SlotItemHandlerFactory {

    SlotItemHandler on(IItemHandler itemHandler, int index, int x, int y);

}
