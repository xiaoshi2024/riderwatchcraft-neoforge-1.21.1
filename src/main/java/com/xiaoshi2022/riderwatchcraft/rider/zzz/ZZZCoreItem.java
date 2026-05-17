package com.xiaoshi2022.riderwatchcraft.rider.zzz;

import com.xiaoshi2022.riderwatchcraft.rider.kuuga.KuugaCoreItem;
import com.xiaoshi2022.riderwatchcraft.registry.riderwatchsSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ZZZCoreItem extends Item implements GeoItem {

    public static final String CORE_ID = "zzz_dream";
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenPlay("open");
    private static final Map<UUID, Long> SOUND_COOLDOWN_MAP = new HashMap<>();
    private static final long COOLDOWN_TICKS = 200L;


    public ZZZCoreItem() {
        super(new Properties()
                .durability(500)
                .fireResistant()
        );
        SingletonGeoAnimatable.registerSyncedAnimatable(this);

    }

    public static String getCoreId() {
        return CORE_ID;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private ZZZCoreItemRenderer renderer;

            @Override
            public GeoItemRenderer<ZZZCoreItem> getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new ZZZCoreItemRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<ZZZCoreItem> controller = new AnimationController<>(this, "press_controller", 1, this::predicate);
        controller.triggerableAnim("open", OPEN_ANIM);
        controllers.add(controller);
    }

    private PlayState predicate(AnimationState<ZZZCoreItem> state) {
        return PlayState.CONTINUE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            long id = GeoItem.getOrAssignId(stack, serverLevel);
            triggerAnim(player, id, "press_controller", "open");

            long currentTick = level.getGameTime();
            UUID playerId = player.getUUID();
            Long lastPlayTick = SOUND_COOLDOWN_MAP.get(playerId);

            if (lastPlayTick == null || currentTick - lastPlayTick >= COOLDOWN_TICKS) {
                SOUND_COOLDOWN_MAP.put(playerId, currentTick);
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        riderwatchsSounds.ZZZ_RIDER_WATCH.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }

        return super.use(level, player, hand);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}