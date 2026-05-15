package com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.Capsem;

import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CapsemModel extends GeoModel<CapsemEntity> {

    @Override
    public ResourceLocation getModelResource(CapsemEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(RiderWatchCraft.MODID, "geo/rider/zzz/capsem.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CapsemEntity animatable) {
        return animatable.getCapsemType().getTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(CapsemEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(RiderWatchCraft.MODID, "animations/rider/zzz/capsem.animation.json");
    }
}