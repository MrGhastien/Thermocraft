package mrghastien.thermocraft.common.network.packets;

import io.netty.buffer.Unpooled;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetwork;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;


/** Packet responsible for carrying data from the server to the client, and tell the client-side container
 * there is an update
 */
public class UpdateHeatNetworkPacket {

    private final FriendlyByteBuf buf;
    private final long id;

    public UpdateHeatNetworkPacket(HeatNetwork net) {
        this.id = net.getId();
        this.buf = new FriendlyByteBuf(Unpooled.buffer());
        net.getBinding().encode(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(id);
        buf.writeBytes(this.buf);
    }

    public UpdateHeatNetworkPacket(FriendlyByteBuf buf) {
        this.id = buf.readLong();
        this.buf = new FriendlyByteBuf(buf.copy());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            HeatNetworkHandler handler = HeatNetworkHandler.instance();
            HeatNetwork net = handler.getClient(id);
            if(net == null) {
                ThermoCraft.LOGGER.warn("Could not synchronize heat network values, as the network is missing on the client");
                return;
            }
            net.getBinding().decode(buf);

        });
        ctx.get().setPacketHandled(true);
    }

}
