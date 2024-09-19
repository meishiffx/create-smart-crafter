package com.tatnux.crafter.modules.crafter.blocks;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;
import static com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer.standardKineticRotationTransform;

public class SmartCrafterRenderer extends SafeBlockEntityRenderer<SmartCrafterBlockEntity> {

    public SmartCrafterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(SmartCrafterBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());

        if (!Backend.canUseInstancing(be.getLevel())) {
            SuperByteBuffer superBuffer = CachedBufferer.partial(AllPartialModels.SHAFTLESS_COGWHEEL, blockState);
            standardKineticRotationTransform(superBuffer, be, light);
            superBuffer.rotateCentered(Direction.UP, (float) (blockState.getValue(HORIZONTAL_FACING)
                    .getAxis() != Direction.Axis.X ? 0 : Math.PI / 2));
            superBuffer.rotateCentered(Direction.EAST, (float) (Math.PI / 2));
            superBuffer.renderInto(ms, vb);
        }
    }

}
