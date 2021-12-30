package mrghastien.thermocraft.common.network.data;

import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Object storing the necessary info to access, mutate and check for changes of a particular value.
 * <p>
 * Effectively acts as a reference to a variable or any value.
 * Not necessarily references a variable, this can be anything as long as a valid {@link DataType} is given.
 *
 * @param <T> The type of the data that will be synchronized
 */
public class DataReference<T> implements Supplier<T>, Consumer<T>, INBTSerializable<INBT> {

    private T lastValue;
    final Supplier<T> getter;
    final Consumer<T> setter;
    private final ResourceLocation id;
    private final DataType<T> type;

    public DataReference(DataType<T> type, ResourceLocation id, Supplier<T> getter, Consumer<T> setter) {
        this.id = id;
        this.getter = getter;
        this.setter = setter;
        this.type = type;
        lastValue = type.getDefaultValue();
    }

    @Override
    public T get() {
        return lastValue;
    }

    @Override
    public void accept(T value) {
        setter.accept(value);
    }

    public void update() {
        lastValue = getter.get();
    }

    public boolean hasChanged() {
        T value = getter.get();
        if(!type.equals(lastValue, value)) {
            lastValue = value;
            return true;
        }
        return false;
    }

    public void encode(PacketBuffer buf) {
        getType().encode(buf, get());
    }

    /**
     * Reads data from the provided buffer and calls {@link #accept(T)}
     * @param buf The byte buffer to read from
     */
    public void accept(PacketBuffer buf) {
        accept(getType().decode(buf));
    }

    public DataType<T> getType() {
        return type;
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public String toString() {
        return "DataReference{" + type + "/" + id + "} = " + lastValue;
    }

    @Override
    public INBT serializeNBT() {
        return getType().serializeNBT(get());
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        accept(getType().deserializeNBT(nbt));
    }
}
