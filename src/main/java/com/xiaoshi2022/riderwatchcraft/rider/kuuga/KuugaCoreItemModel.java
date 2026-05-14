package com.xiaoshi2022.riderwatchcraft.rider.kuuga;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KuugaCoreItemModel extends GeoModel<KuugaCoreItem> {

    @Override
    public ResourceLocation getModelResource(KuugaCoreItem object) {
        return ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "geo/item/kuuga_core.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KuugaCoreItem object) {
        return ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "textures/item/kuuga_core.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KuugaCoreItem animatable) {
        return ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "animations/item/kuuga_core.animation.json");
    }
}