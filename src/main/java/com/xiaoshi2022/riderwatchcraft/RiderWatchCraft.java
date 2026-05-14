package com.xiaoshi2022.riderwatchcraft;

import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreInteractionHandler;
import com.xiaoshi2022.riderwatchcraft.rider.kuuga.KuugaCoreItem;
import com.xiaoshi2022.riderwatchcraft.rider.kuuga.KuugaRegistry;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.ZZZCoreItem;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.ZZZRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.GeckoLib;

@Mod(RiderWatchCraft.MODID)
public class RiderWatchCraft {
    public static final String MODID = "riderwatchcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<ZZZCoreItem> ZZZ_DREAM_CORE = ITEMS.register("zzz_dream_core", ZZZCoreItem::new);
    public static final DeferredItem<KuugaCoreItem> KUUGA_MIGHTY_CORE = ITEMS.register("kuuga_mighty_core", KuugaCoreItem::new);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RIDERS_TAB = CREATIVE_MODE_TABS.register("riders_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.riderwatchcraft.riders"))
                    .withTabsBefore(CreativeModeTabs.BUILDING_BLOCKS)
                    .icon(() -> KUUGA_MIGHTY_CORE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ZZZ_DREAM_CORE.get());
                        output.accept(KUUGA_MIGHTY_CORE.get());
                    }).build()
    );

    public RiderWatchCraft(IEventBus modEventBus, ModContainer modContainer) {

        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        modEventBus.register(ZZZRegistry.class);
        modEventBus.register(KuugaRegistry.class);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("RiderWatchCraft initialized");
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();

        // 1. 首先忽略 Shift+右键（无论是在客户端还是服务端）
        if (player.isShiftKeyDown()) {
            return;
        }

        // 2. 只在按下 Alt 键时才处理
        if (!isAltPressed(player)) {
            return;
        }

        // 3. 调用前置模组的处理方法
        CoreInteractionHandler.handleRightClick(event);
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();

        if (player.isShiftKeyDown()) {
            return;
        }

        if (!isAltPressed(player)) {
            return;
        }

        CoreInteractionHandler.handleRightClickBlock(event);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server starting");
    }

    /**
     * 检查 Alt 键是否按下
     * 客户端：直接检查键盘状态
     * 服务端：从 NBT 或数据包中获取（这里简化处理，服务端信任客户端的过滤）
     */
    private boolean isAltPressed(Player player) {
        // 服务端直接返回 true（因为已经在客户端过滤过了）
        if (!player.level().isClientSide) {
            return true;
        }

        // 客户端检查 Alt 键
        return isAltPressedClient();
    }

    @OnlyIn(Dist.CLIENT)
    private boolean isAltPressedClient() {
        try {
            long window = Minecraft.getInstance().getWindow().getWindow();
            boolean leftAlt = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS;
            boolean rightAlt = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
            return leftAlt || rightAlt;
        } catch (Exception e) {
            return false;
        }
    }
}