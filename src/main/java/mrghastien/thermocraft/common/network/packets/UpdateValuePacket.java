package mrghastien.thermocraft.common.network.packets;

import mrghastien.thermocraft.common.network.NetworkDataType;
import mrghastien.thermocraft.common.network.NetworkHandler;
import mrghastien.thermocraft.common.network.data.INetworkData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateValuePacket {

    private final NetworkDataType type;
    private final Object value;
    private final int id;
    private final boolean manual;

    public UpdateValuePacket(INetworkData data, int id, boolean manual) {
        this.type = data.getType();
        this.value = data.get();
        this.id = id;
        this.manual = manual;
    }

    public void encode(PacketBuffer buf) {
        buf.writeEnum(type);
        buf.writeInt(id);
        type.encode(buf, value == null ? type.getDefaultValue() : value);
        buf.writeBoolean(manual);
    }

    public UpdateValuePacket(PacketBuffer buf) {
        this.type = buf.readEnum(NetworkDataType.class);
        this.id = buf.readInt();
        this.value = type.decode(buf);
        this.manual = buf.readBoolean();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> NetworkHandler.getInstance(LogicalSide.CLIENT).updateValue(value, id));
        ctx.get().setPacketHandled(true);
    }

}
