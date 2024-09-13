package com.tatnux.crafter.lib.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockBase extends Block {

    public BlockBase() {
        super(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK));
    }
}
