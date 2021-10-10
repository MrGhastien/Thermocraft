package mrghastien.thermocraft.common.network.packets;

import mrghastien.thermocraft.common.ThermoCraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.IPacket;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PacketHandler {

    public static final PacketDistributor<Container> CONTAINER_LISTENERS = new PacketDistributor<>(PacketHandler::playerListContainerConsumer, NetworkDirection.PLAY_TO_CLIENT);

    public static final String PROTOCOL_VERSION = String.valueOf(1);
    private static int ID = 0;

    private static int nextID() {
        return ID++ - 1;
    }

    public static final SimpleChannel MAIN_CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(ThermoCraft.MODID, "main_channel"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    public static void registerNetworkPackets() {

        MAIN_CHANNEL.messageBuilder(UpdateValuePacket.class, nextID())
                .encoder(UpdateValuePacket::encode)
                .decoder(UpdateValuePacket::new)
                .consumer(UpdateValuePacket::handle)
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

        MAIN_CHANNEL.messageBuilder(ModUpdateTileEntityPacket.class, nextID())
                .encoder(ModUpdateTileEntityPacket::encode)
                .decoder(ModUpdateTileEntityPacket::new)
                .consumer(ModUpdateTileEntityPacket::handle)
                .add();
    }

    public static void sendToPlayer(Object message, ServerPlayerEntity listener) {
        MAIN_CHANNEL.send(PacketDistributor.PLAYER.with(() -> listener), message);
    }

    public static void sendToPlayers(Object message, BlockPos pos, double radius, RegistryKey<World> dim) {
        MAIN_CHANNEL.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), radius, dim)), message);
    }

    public static void sendToDimension(Object message, RegistryKey<World> dimension) {
        MAIN_CHANNEL.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

    public static void sendToServer(Object message) {
        MAIN_CHANNEL.sendToServer(message);
    }

    private static Consumer<IPacket<?>> playerListContainerConsumer(final PacketDistributor<Container> distributor, final Supplier<Container> containerSupplier) {
        return p -> {
            Container c = containerSupplier.get();
            for(IContainerListener listener : c.containerListeners) {
                if (listener instanceof ServerPlayerEntity)
                    ((ServerPlayerEntity)listener).connection.connection.send(p);
            }
        };
    }

    public static PacketDistributor.PacketTarget containerListenersTarget(Container c) {
        return CONTAINER_LISTENERS.with(() -> c);
    }

}
