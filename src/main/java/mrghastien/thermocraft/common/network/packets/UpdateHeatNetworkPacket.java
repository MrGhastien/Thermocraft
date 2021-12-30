package mrghastien.thermocraft.common.network.packets;

import io.netty.buffer.Unpooled;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetwork;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.inventory.containers.BaseContainer;
import mrghastien.thermocraft.common.network.INetworkBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


/** Packet responsible for carrying data from the server to the client, and tell the client-side container
 * there is an update
 */
public class UpdateHeatNetworkPacket {

    private final PacketBuffer buf;
    private final long id;

    public UpdateHeatNetworkPacket(HeatNetwork net) {
        this.id = net.getId();
        this.buf = new PacketBuffer(Unpooled.buffer());
        net.getBinding().encode(buf);
    }

    public void encode(PacketBuffer buf) {
        buf.writeLong(id);
        buf.writeBytes(this.buf);
    }

    public UpdateHeatNetworkPacket(PacketBuffer buf) {
        this.id = buf.readLong();
        this.buf = new PacketBuffer(buf.copy());
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
