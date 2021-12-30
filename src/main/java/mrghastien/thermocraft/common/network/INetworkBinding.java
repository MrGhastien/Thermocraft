package mrghastien.thermocraft.common.network;

import net.minecraft.network.PacketBuffer;


/**
 * Binding allowing for communicating values over the network.
 *
 * <p>If a value has to be synchronized, it is written into a ByteBuffer by the
 * {@link INetworkBinding#encode(PacketBuffer)} method. This buffer can then be given to a packet object.</p>
 * Data inside a buffer (possibly originating from a packet) is read from the ByteBuffer in the
 * {@link INetworkBinding#decode(PacketBuffer)} method.
 */
public interface INetworkBinding {

    boolean hasChanged();

    /**
     * Encodes data to the provided {@link io.netty.buffer.ByteBuf}.
     * @param buf The buffer to write to.
     * @implSpec This operation must be supported.
     */
    void encode(PacketBuffer buf);

    /**
     * Reads and decodes data contained in the provided {@link io.netty.buffer.ByteBuf}.
     * @param buf The buffer to read from.
     * @implSpec This operation must be supported.
     */
    void decode(PacketBuffer buf);

}
