package mrghastien.thermocraft.common.network.packets;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetwork;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InvalidateClientHeatNetworkPacket {

    private final long networkId;

    public InvalidateClientHeatNetworkPacket(long id) {
        this.networkId = id;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(networkId);
    }

    public InvalidateClientHeatNetworkPacket(FriendlyByteBuf buf) {
        this.networkId = buf.readLong();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            HeatNetwork net = HeatNetworkHandler.instance().getClient(networkId);
            if(net == null) ThermoCraft.LOGGER.warn("Couldn't invalidate client heat network  with id " + networkId + " , as it doesn't exist");
            else HeatNetworkHandler.instance().invalidateClientNetwork(net);
        });
        ctx.get().setPacketHandled(true);
    }
}
