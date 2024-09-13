package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.foundation.block.IBE;
import com.tatnux.crafter.lib.blocks.BlockBase;
import com.tatnux.crafter.modules.crafter.CrafterModule;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class CrafterBlock extends Block implements IBE<CrafterBlockEntity> {

    public CrafterBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos,
                                          Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult ray) {
        if (player.isCrouching()) {
            return InteractionResult.PASS;
        }

        if (player instanceof FakePlayer) {
            return InteractionResult.PASS;
        }

        if (world.isClientSide){
            return InteractionResult.SUCCESS;
        }

        withBlockEntityDo(world, pos,
                crafter -> NetworkHooks.openScreen((ServerPlayer) player, crafter, crafter::sendToMenu));
        return InteractionResult.SUCCESS;
    }

    @Override
    public Class<CrafterBlockEntity> getBlockEntityClass() {
        return CrafterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CrafterBlockEntity> getBlockEntityType() {
        return CrafterModule.CRAFTER_BLOCK_ENTITY.get();
    }
}
