package mrghastien.thermocraft.common.network.packets;

import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetwork;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CreateClientHeatNetworkPacket {

    private final HeatNetworkHandler.HeatNetworkType type;
    private final long networkId;

    public CreateClientHeatNetworkPacket(HeatNetwork net) {
        this.type = net.type();
        this.networkId = net.getId();
    }

    public void encode(PacketBuffer buf) {
        buf.writeEnum(type);
        buf.writeLong(networkId);
    }

    public CreateClientHeatNetworkPacket(PacketBuffer buf) {
        this.type = buf.readEnum(HeatNetworkHandler.HeatNetworkType.class);
        this.networkId = buf.readLong();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            HeatNetworkHandler handler = HeatNetworkHandler.instance();
            handler.createClient(Minecraft.getInstance().level, type, networkId);
        });
        ctx.get().setPacketHandled(true);
    }
}
