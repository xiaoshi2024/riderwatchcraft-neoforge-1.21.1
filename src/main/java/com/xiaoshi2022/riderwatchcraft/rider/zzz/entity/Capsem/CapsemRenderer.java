package com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.Capsem;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CapsemRenderer extends GeoEntityRenderer<CapsemEntity> {

    public CapsemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CapsemModel());
        this.shadowRadius = 0.2f;
    }

    @Override
    protected float getDeathMaxRotation(CapsemEntity entityLivingBaseIn) {
        return 0.0F;
    }
}