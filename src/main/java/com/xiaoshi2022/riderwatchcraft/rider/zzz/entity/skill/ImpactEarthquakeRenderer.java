package com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.skill;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ImpactEarthquakeRenderer extends EntityRenderer<ImpactEarthquakeEntity> {

    public ImpactEarthquakeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(ImpactEarthquakeEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

        var blockInstances = entity.getBlockInstances();

        for (var entry : blockInstances.entrySet()) {
            var instance = entry.getValue();
            BlockPos pos = instance.getPos();
            BlockState blockState = instance.getBlockState();

            if (blockState.isAir()) continue;

            renderFlippingBlock(entity, instance, pos, blockState, partialTicks, poseStack, bufferSource);
        }
    }

    private void renderFlippingBlock(ImpactEarthquakeEntity entity,
                                     ImpactEarthquakeEntity.EarthquakeBlockInstance instance,
                                     BlockPos pos,
                                     BlockState blockState,
                                     float partialTicks,
                                     PoseStack poseStack,
                                     MultiBufferSource bufferSource) {

        Vec3 entityPos = entity.position();
        double offsetX = pos.getX() - entityPos.x + 0.5;
        double offsetZ = pos.getZ() - entityPos.z + 0.5;

        float progress = instance.getAnimationProgress(partialTicks);
        float heightOffset = instance.getHeightOffset(progress);
        float rotationAngle = instance.getRotationAngle(progress);

        poseStack.pushPose();

        poseStack.translate(offsetX, pos.getY() - entityPos.y + heightOffset, offsetZ);

        Vec3 direction = instance.getDirection();
        float horizontalAngle = (float) Math.atan2(direction.x, direction.z);

        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.YP.rotation(horizontalAngle));
        poseStack.mulPose(Axis.XP.rotationDegrees(rotationAngle));

        float wobble = (float) Math.sin(progress * Math.PI * 3) * 8.0F * (1 - progress);
        poseStack.mulPose(Axis.ZP.rotationDegrees(wobble));

        poseStack.translate(0, -0.5, 0);

        float scale = 1.0F + (float) Math.sin(progress * Math.PI) * 0.15F;
        poseStack.scale(scale, scale, scale);

        BlockPos lightPos = pos.above(2);
        int light = LightTexture.pack(
                entity.level().getMaxLocalRawBrightness(lightPos),
                entity.level().getLightEmission(lightPos)
        );

        var renderer = Minecraft.getInstance().getBlockRenderer();
        renderer.renderSingleBlock(
                blockState,
                poseStack,
                bufferSource,
                light,
                OverlayTexture.NO_OVERLAY,
                net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
                null
        );

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ImpactEarthquakeEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(ImpactEarthquakeEntity entity, Frustum frustum, double x, double y, double z) {
        return true;  // 关键：总是渲染
    }
}