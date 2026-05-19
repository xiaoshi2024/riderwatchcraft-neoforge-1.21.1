package com.xiaoshi2022.riderwatchcraft.network;

import com.xiaoshi2022.kamenriderweaponcraft.Item.custom.Heiseisword;
import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreSlotManager;
import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CoreOperationPacket(String operation, String coreId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CoreOperationPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "core_operation"));

    public static final StreamCodec<FriendlyByteBuf, CoreOperationPacket> STREAM_CODEC =
            StreamCodec.ofMember(CoreOperationPacket::encode, CoreOperationPacket::decode);

    public CoreOperationPacket(String operation, String coreId) {
        this.operation = operation;
        this.coreId = coreId;
    }

    private void encode(FriendlyByteBuf buf) {
        buf.writeUtf(operation);
        buf.writeUtf(coreId != null ? coreId : "");
    }

    private static CoreOperationPacket decode(FriendlyByteBuf buf) {
        String operation = buf.readUtf();
        String coreId = buf.readUtf();
        return new CoreOperationPacket(operation, coreId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleServer(CoreOperationPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player == null) return;

            ItemStack mainhand = player.getMainHandItem();
            if (!Heiseisword.isHeiseisword(mainhand)) {
                return;
            }

            boolean hasCore = CoreSlotManager.hasAttachedCore(mainhand);
            String currentCoreId = hasCore ? CoreSlotManager.getAttachedCoreId(mainhand) : null;

            if ("install".equals(packet.operation)) {
                // 检查是否已经有外部核心
                if (hasCore) {
                    // 已经有核心，不能重复安装
                    return;
                }

                // 检查副手是否有核心物品
                if (CoreSlotManager.isValidCore(packet.coreId)) {
                    CoreSlotManager.attachCore(mainhand, packet.coreId);
                    player.getOffhandItem().shrink(1);
                }

            } else if ("detach".equals(packet.operation)) {
                // 检查是否有核心可以拆下
                if (!hasCore) {
                    // 没有核心，不能拆下
                    return;
                }

                // 拆下核心
                CoreSlotManager.detachCore(mainhand);

                ItemStack coreStack = getCoreItemStack(currentCoreId);
                if (!coreStack.isEmpty()) {
                    if (!player.addItem(coreStack)) {
                        player.drop(coreStack, false);
                    }
                }
            }
        });
    }

    private static ItemStack getCoreItemStack(String coreId) {
        if ("kuuga_mighty".equals(coreId)) {
            return new ItemStack(RiderWatchCraft.KUUGA_MIGHTY_CORE.get());
        }
        if ("zzz_dream".equals(coreId)) {
            return new ItemStack(RiderWatchCraft.ZZZ_DREAM_CORE.get());
        }
        if ("decade_form".equals(coreId)) {  // ← 添加这个
            return new ItemStack(RiderWatchCraft.DECADE_FORM_CORE.get());
        }
        return ItemStack.EMPTY;
    }
}