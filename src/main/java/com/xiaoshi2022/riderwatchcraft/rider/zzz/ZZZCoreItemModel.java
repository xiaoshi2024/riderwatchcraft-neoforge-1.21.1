package com.xiaoshi2022.riderwatchcraft.rider.zzz;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ZZZCoreItemModel extends GeoModel<ZZZCoreItem> {

    @Override
    public ResourceLocation getModelResource(ZZZCoreItem object) {
        return ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "geo/item/zzz_core.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ZZZCoreItem object) {
        return ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "textures/item/zzz_core.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ZZZCoreItem animatable) {
        return ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "animations/item/zzz_core.animation.json");
    }
}