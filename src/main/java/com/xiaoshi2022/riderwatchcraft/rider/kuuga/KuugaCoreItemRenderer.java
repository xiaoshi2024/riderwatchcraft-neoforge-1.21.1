package com.xiaoshi2022.riderwatchcraft.rider.kuuga;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class KuugaCoreItemRenderer extends GeoItemRenderer<KuugaCoreItem> {

    public KuugaCoreItemRenderer() {
        super(new KuugaCoreItemModel());
    }

    @Override
    public ResourceLocation getTextureLocation(KuugaCoreItem animatable) {
        return ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "textures/item/kuuga_core.png");
    }
}