package com.xiaoshi2022.riderwatchcraft.mixin;

import com.xiaoshi2022.kamenriderweaponcraft.Item.client.Heiseisword.HeiseiswordModel;
import com.xiaoshi2022.kamenriderweaponcraft.Item.custom.Heiseisword;
import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreSlotManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HeiseiswordModel.class, remap = false)
public abstract class HeiseiswordModelMixin {

    @Inject(method = "getModelResource", at = @At("HEAD"), cancellable = true)
    private void riderwatchcraft$getModelResource(Heiseisword object, CallbackInfoReturnable<ResourceLocation> cir) {
        ItemStack stack = getCurrentHeiseiswordStack();
        if (stack != null) {
            ResourceLocation externalModel = CoreSlotManager.getAttachedCoreModel(stack);
            if (externalModel != null) {
                cir.setReturnValue(externalModel);
            }
        }
    }

    @Inject(method = "getTextureResource", at = @At("HEAD"), cancellable = true)
    private void riderwatchcraft$getTextureResource(Heiseisword object, CallbackInfoReturnable<ResourceLocation> cir) {
        ItemStack stack = getCurrentHeiseiswordStack();
        if (stack != null) {
            ResourceLocation externalModel = CoreSlotManager.getAttachedCoreModel(stack);
            if (externalModel != null) {
                cir.setReturnValue(ResourceLocation.fromNamespaceAndPath(
                        externalModel.getNamespace(),
                        "textures/item/" + externalModel.getPath().replace("geo/item/", "").replace(".geo.json", ".png")
                ));
            }
        }
    }

    @Inject(method = "getAnimationResource", at = @At("HEAD"), cancellable = true)
    private void riderwatchcraft$getAnimationResource(Heiseisword animatable, CallbackInfoReturnable<ResourceLocation> cir) {
        ItemStack stack = getCurrentHeiseiswordStack();
        if (stack != null) {
            var animController = CoreSlotManager.getAttachedCoreAnimController(stack);
            if (animController != null) {
                cir.setReturnValue(animController.get());
            }
        }
    }

    private ItemStack getCurrentHeiseiswordStack() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return null;

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof Heiseisword) {
            return mainHand;
        }

        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof Heiseisword) {
            return offHand;
        }

        return null;
    }
}