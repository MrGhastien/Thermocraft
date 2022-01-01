package mrghastien.thermocraft.common.network;

import mrghastien.thermocraft.common.network.data.DataReference;
import mrghastien.thermocraft.common.network.data.DataType;
import mrghastien.thermocraft.common.network.data.IDataHolder;
import mrghastien.thermocraft.common.network.data.ReadOnlyDataHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default implementation of a network binding.
 * <p>
 * Writes only modified values to the provided {@link io.netty.buffer.ByteBuf buffers}, and reads all the data written in the buffer when decoding.
 * <p>
 * This object will not modify the underlying holder in any way, but can change specific values through {@link DataReference data references}.
 *
 * @see DataReference
 * @see INetworkBinding
 * @see IDataHolder
 * @see ReadOnlyDataHolder
 */
public class CompositeDataNetworkBinding implements INetworkBinding {

    private final Map<ResourceLocation, DataReference<?>> dirtyMap = new LinkedHashMap<>();

    private final ReadOnlyDataHolder holder;

    public CompositeDataNetworkBinding(IDataHolder holder) {
        this.holder = new ReadOnlyDataHolder(holder);
    }

    public boolean hasChanged() {
        //Put the modified data in a temporary map
        holder.forEach((id, data) -> {if (data.hasChanged()) dirtyMap.put(id, data);});
        return dirtyMap.size() != 0;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        //Encode the modified data in the buffer
        for (Map.Entry<ResourceLocation, DataReference<?>> entry : dirtyMap.entrySet()) {
            DataReference<?> ref = entry.getValue();
            buf.writeUtf(ref.getType().getFullName()); //Safety
            buf.writeResourceLocation(entry.getKey());
            ref.encode(buf);
        }
        dirtyMap.clear();
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        //While there is still data in the buffer, decode it and update the container
        while (buf.isReadable()) {
            DataType<?> expected = DataType.fromString(buf.readUtf());
            DataReference<?> ref = holder.getData(buf.readResourceLocation());
            if(ref == null)
                throw new IllegalStateException("DataReference not registered on the client");
            //For safety; check if the type of the client data reference is the same as the server before doing anything
            if(ref.getType() != expected)
                throw new DataTypeMismatchException(expected, ref.getType());
            ref.accept(buf);
        }
    }
}
