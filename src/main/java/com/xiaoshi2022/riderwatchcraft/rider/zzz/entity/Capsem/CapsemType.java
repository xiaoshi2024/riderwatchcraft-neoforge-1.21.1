package com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.Capsem;

import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import net.minecraft.resources.ResourceLocation;

public enum CapsemType {
    ERASE("erase", 0x8B0000),
    IMPACT("impact", 0xFF8C00);

    private final String name;
    private final int color;

    CapsemType(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public ResourceLocation getTexture() {
        return ResourceLocation.fromNamespaceAndPath(RiderWatchCraft.MODID, "textures/rider/zzz/" + name + "_capsem.png");
    }
}