package com.xiaoshi2022.riderwatchcraft.rider.zzz;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ZZZCoreItemRenderer extends GeoItemRenderer<ZZZCoreItem> {

    public ZZZCoreItemRenderer() {
        super(new ZZZCoreItemModel());
    }

    @Override
    public ResourceLocation getTextureLocation(ZZZCoreItem animatable) {
        return ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "textures/item/zzz_core.png");
    }
}