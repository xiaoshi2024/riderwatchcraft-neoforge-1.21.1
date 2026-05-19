package com.xiaoshi2022.riderwatchcraft.rider.decade;

import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DecadeCoreItemModel extends GeoModel<DecadeCoreItem> {
    @Override
    public ResourceLocation getModelResource(DecadeCoreItem object) {
        return ResourceLocation.fromNamespaceAndPath(RiderWatchCraft.MODID, "geo/item/decade_core.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DecadeCoreItem object) {
        return ResourceLocation.fromNamespaceAndPath(RiderWatchCraft.MODID, "textures/item/decade_core.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DecadeCoreItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(RiderWatchCraft.MODID, "animations/item/decade_core.animation.json");
    }
}