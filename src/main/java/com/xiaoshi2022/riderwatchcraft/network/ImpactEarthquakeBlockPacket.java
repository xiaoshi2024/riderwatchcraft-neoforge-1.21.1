package com.xiaoshi2022.riderwatchcraft.network;

import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.skill.ImpactEarthquakeEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ImpactEarthquakeBlockPacket(
        int entityId,
        BlockPos pos,
        ResourceLocation blockId,
        double dirX, double dirY, double dirZ
) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(
            RiderWatchCraft.MODID, "impact_earthquake_block");

    public static final Type<ImpactEarthquakeBlockPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<ByteBuf, ImpactEarthquakeBlockPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ImpactEarthquakeBlockPacket::entityId,
            BlockPos.STREAM_CODEC,
            ImpactEarthquakeBlockPacket::pos,
            ResourceLocation.STREAM_CODEC,
            ImpactEarthquakeBlockPacket::blockId,
            ByteBufCodecs.DOUBLE,
            ImpactEarthquakeBlockPacket::dirX,
            ByteBufCodecs.DOUBLE,
            ImpactEarthquakeBlockPacket::dirY,
            ByteBufCodecs.DOUBLE,
            ImpactEarthquakeBlockPacket::dirZ,
            ImpactEarthquakeBlockPacket::new
    );

    // 便捷构造方法 - 从 BlockState 创建
    public ImpactEarthquakeBlockPacket(int entityId, BlockPos pos, BlockState state, Vec3 direction) {
        this(entityId, pos, BuiltInRegistries.BLOCK.getKey(state.getBlock()),
                direction.x, direction.y, direction.z);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public Vec3 getDirection() {
        return new Vec3(dirX, dirY, dirZ);
    }

    public BlockState getBlockState() {
        Block block = BuiltInRegistries.BLOCK.get(blockId);
        return block.defaultBlockState();
    }

    public static void handleClient(ImpactEarthquakeBlockPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player == null) return;

            var level = player.level();
            var entity = level.getEntity(packet.entityId());

            if (entity instanceof ImpactEarthquakeEntity earthquake) {
                earthquake.addBlockInstance(packet.pos(), packet.getBlockState(), packet.getDirection());
            }
        });
    }
}