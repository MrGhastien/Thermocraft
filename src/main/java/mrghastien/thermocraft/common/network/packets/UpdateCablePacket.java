package mrghastien.thermocraft.common.network.packets;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Cable;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetwork;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.util.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateCablePacket {

    //private final HeatNetworkHandler.HeatNetworkType type;
    private final BlockPos pos;
    private final UpdateType type;
    private final CompoundTag data;

    public UpdateCablePacket(Cable c, UpdateType type) {
        HeatNetwork net = c.getNetwork();
        this.data = new CompoundTag();
        switch (type) {
            case NETWORK:
                data.putLong("id", net == null ? -1 : net.getId());
                break;
            case CONNECTIONS:
                c.writeToNbt(data);
                break;
        }
        this.pos = c.getPos();
        this.type = type;
    }

    UpdateCablePacket(FriendlyByteBuf buf) {
        this.type = buf.readEnum(UpdateType.class);
        this.data = buf.readNbt();
        this.pos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        buf.writeNbt(data);
        buf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            HeatNetworkHandler handler = HeatNetworkHandler.instance();
            Cable c = ModUtils.getCable(pos, Minecraft.getInstance().level);
            if(c == null) {
                ThermoCraft.LOGGER.warn("No cable at pos " + pos, new NullPointerException());
                return;
            }
            switch (type) {
                case NETWORK:
                    long id = data.getLong("id");
                    HeatNetwork newNetwork = handler.getClient(id);
                    if(newNetwork == null && id != -1) {
                        ThermoCraft.LOGGER.warn("Heat network's client copy wasn't created", new IllegalStateException());
                    } else {
                        HeatNetworkHandler.instance().addToNetwork(newNetwork, c);
                    }
                    break;
                case CONNECTIONS:
                    c.handleUpdateTag(data);
                    c.getTileEntity().requestModelDataUpdate();
                    break;
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public enum UpdateType {
        NETWORK,
        CONNECTIONS;
    }

}
