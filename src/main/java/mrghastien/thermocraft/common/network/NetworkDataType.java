package mrghastien.thermocraft.common.network;

import mrghastien.thermocraft.common.network.data.*;
import mrghastien.thermocraft.util.TriFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.extensions.IForgePacketBuffer;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public enum NetworkDataType {
    INT(Integer.TYPE, 0, (buf, value) -> buf.writeInt((int) value), PacketBuffer::readInt,
            (i, g, s) -> new NetworkInt(i, () -> (int) g.get(), s::accept)),

    LONG(Long.TYPE, 0, (buf, value) -> buf.writeLong((long) value), PacketBuffer::readLong,
            (i, g, s) -> new NetworkLong(i, () -> (long) g.get(), s::accept)),

    FLOAT(Float.TYPE, 0, (buf, value) -> buf.writeFloat((float) value), PacketBuffer::readFloat,
            (i, g, s) -> new NetworkFloat(i, () -> (float) g.get(), s::accept)),

    DOUBLE(Double.TYPE, 0, (buf, value) -> buf.writeDouble((double) value), PacketBuffer::readDouble,
            (i, g, s) -> new NetworkDouble(i, () -> (double) g.get(), s::accept)),

    BOOLEAN(Boolean.TYPE, false, (buf, value) -> buf.writeBoolean((boolean) value), PacketBuffer::readBoolean,
            (i, g, s) -> new NetworkBoolean(i, () -> (boolean) g.get(), s::accept)),

    STRING(String.class, "", (buf, value) -> buf.writeUtf((String) value), buf -> buf.readUtf(32767),
            (i, g, s) -> new NetworkString(i, () -> (String) g.get(), s::accept)),

    ITEMSTACK(ItemStack.class, ItemStack.EMPTY, (buf, value) -> buf.writeItem((ItemStack) value), PacketBuffer::readItem,
            (i, g, s) -> new NetworkItemStack(i, () -> (ItemStack) g.get(), s::accept)),

    FLUIDSTACK(FluidStack.class, FluidStack.EMPTY, (buf, value) -> buf.writeFluidStack((FluidStack) value), IForgePacketBuffer::readFluidStack,
            (i, g, s) -> new NetworkFluidStack(i, () -> (FluidStack) g.get(), s::accept)),
    LIST(List.class, null, NetworkDataType::encodeCollection, NetworkDataType::decodeList, null),
    NON_PRIMITIVE(null, null, null, null, null);

    private final Class<?> type;
    private final Object defaultValue;
    private final BiConsumer<PacketBuffer, Object> encoder;
    private final Function<PacketBuffer, Object> decoder;
    private final TriFunction<Integer, Supplier<Object>, Consumer<Object>, INetworkData> dataConstructor; //Constructor of INetworkData

    NetworkDataType(Class<?> type, Object defaultValue, BiConsumer<PacketBuffer, Object> encoder, Function<PacketBuffer, Object> decoder,
                    TriFunction<Integer, Supplier<Object>, Consumer<Object>, INetworkData> dataConstructor) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.encoder = encoder;
        this.decoder = decoder;
        this.dataConstructor = dataConstructor;
    }

    public static NetworkDataType getFromClass(Class<?> type) {
        for(NetworkDataType dataType : values()) {
            if(type == dataType.type) return dataType;
        }
        return NetworkDataType.NON_PRIMITIVE;
    }

    public INetworkData createData(int id, Supplier<Object> getter, Consumer<Object> setter) {
        if(dataConstructor == null) throw new UnsupportedOperationException();
        return dataConstructor.apply(id, getter, setter);
    }

    public NetworkCollection createCollection(int id, Supplier<List<Object>> getter, Function<Integer, Object> elementGetter, BiConsumer<Integer, Object> elementSetter) {
        return new NetworkCollection(this, getter, elementGetter, elementSetter, id);
    }

    public void encode(PacketBuffer buf, Object value) {
        encoder.accept(buf, value);
    }

    public Object decode(PacketBuffer buf) {
        return decoder.apply(buf);
    }

    public Class<?> getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    private static void encodeCollection(PacketBuffer buf, Object value) {
        Collection<?> c = (Collection<?>) value;
        buf.writeInt(c.size());
        NetworkDataType type = null;
        for(Object o : c) {
            if(type == null) type = getFromClass(o.getClass());
            type.encode(buf, o);
        }
        buf.writeEnum(type);
    }

    private static List<Object> decodeList(PacketBuffer buf) {
        int size = buf.readInt();
        NetworkDataType type = buf.readEnum(NetworkDataType.class);
        List<Object> set = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            set.add(i, type.decode(buf));
        }
        return set;
    }
}
