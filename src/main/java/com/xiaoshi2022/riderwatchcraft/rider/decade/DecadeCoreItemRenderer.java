package com.xiaoshi2022.riderwatchcraft.rider.decade;

import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DecadeCoreItemRenderer extends GeoItemRenderer<DecadeCoreItem> {
    public DecadeCoreItemRenderer() {
        super(new DecadeCoreItemModel());
    }

    @Override
    public ResourceLocation getTextureLocation(DecadeCoreItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(RiderWatchCraft.MODID, "textures/item/decade_core.png");
    }
}