package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import com.tatnux.crafter.modules.crafter.SmartCrafterModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class SmartCrafterBlock extends HorizontalKineticBlock implements IBE<SmartCrafterBlockEntity>, ICogWheel {

    public SmartCrafterBlock(Properties pProperties) {
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

        if (AllItems.WRENCH.isIn(player.getItemInHand(hand))) {
            return InteractionResult.PASS;
        }

        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        this.withBlockEntityDo(world, pos,
                crafter -> NetworkHooks.openScreen((ServerPlayer) player, crafter, crafter::sendToMenu));
        return InteractionResult.SUCCESS;
    }

    @Override
    public Class<SmartCrafterBlockEntity> getBlockEntityClass() {
        return SmartCrafterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmartCrafterBlockEntity> getBlockEntityType() {
        return SmartCrafterModule.SMART_CRAFTER_BLOCK_ENTITY.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }
}
