package mrghastien.thermocraft.common.network.packets;

import mrghastien.thermocraft.common.ThermoCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PacketHandler {

    public static final PacketDistributor<Collection<LevelChunk>> CONTAINER_LISTENERS = new PacketDistributor<>(PacketHandler::trackingAnyChunkConsumer, NetworkDirection.PLAY_TO_CLIENT);

    public static final String PROTOCOL_VERSION = String.valueOf(1);
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static final SimpleChannel MAIN_CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(ThermoCraft.MODID, "main_channel"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    public static void registerNetworkPackets() {

        MAIN_CHANNEL.messageBuilder(UpdateClientContainerPacket.class, nextID())
                .encoder(UpdateClientContainerPacket::encode)
                .decoder(UpdateClientContainerPacket::new)
                .consumer(UpdateClientContainerPacket::handle)
                .add();

        MAIN_CHANNEL.messageBuilder(CreateClientHeatNetworkPacket.class, nextID())
                .encoder(CreateClientHeatNetworkPacket::encode)
                .decoder(CreateClientHeatNetworkPacket::new)
                .consumer(CreateClientHeatNetworkPacket::handle)
                .add();

        MAIN_CHANNEL.messageBuilder(InvalidateClientHeatNetworkPacket.class, nextID())
                .encoder(InvalidateClientHeatNetworkPacket::encode)
                .decoder(InvalidateClientHeatNetworkPacket::new)
                .consumer(InvalidateClientHeatNetworkPacket::handle)
                .add();

        MAIN_CHANNEL.messageBuilder(UpdateCablePacket.class, nextID())
                .encoder(UpdateCablePacket::encode)
                .decoder(UpdateCablePacket::new)
                .consumer(UpdateCablePacket::handle)
                .add();

        MAIN_CHANNEL.messageBuilder(ModUpdateBlockEntityPacket.class, nextID())
                .encoder(ModUpdateBlockEntityPacket::encode)
                .decoder(ModUpdateBlockEntityPacket::new)
                .consumer(ModUpdateBlockEntityPacket::handle)
                .add();

        MAIN_CHANNEL.messageBuilder(UpdateHeatNetworkPacket.class, nextID())
                .encoder(UpdateHeatNetworkPacket::encode)
                .decoder(UpdateHeatNetworkPacket::new)
                .consumer(UpdateHeatNetworkPacket::handle)
                .add();
    }

    public static void sendToPlayer(Object message, ServerPlayer listener) {
        MAIN_CHANNEL.send(PacketDistributor.PLAYER.with(() -> listener), message);
    }

    public static void sendToPlayers(Object message, BlockPos pos, double radius, ResourceKey<Level> dim) {
        MAIN_CHANNEL.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), radius, dim)), message);
    }

    public static void sendToDimension(Object message, ResourceKey<Level> dimension) {
        MAIN_CHANNEL.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

    public static void sendToServer(Object message) {
        MAIN_CHANNEL.sendToServer(message);
    }

    private static Consumer<Packet<?>> trackingAnyChunkConsumer(final PacketDistributor<Collection<LevelChunk>> distributor, final Supplier<Collection<LevelChunk>> supplier) {
        return p -> {
            Set<ServerPlayer> alreadySentTargets = new HashSet<>();
            for (LevelChunk chunk : supplier.get()) {
                List<ServerPlayer> players = ((ServerChunkCache)chunk.getLevel().getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false);
                for(ServerPlayer player : players) {
                    if(alreadySentTargets.add(player))
                        player.connection.send(p);
                }
            }
        };
    }

}
