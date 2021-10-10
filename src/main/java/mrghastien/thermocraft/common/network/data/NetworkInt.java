package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.network.NetworkDataType;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class NetworkInt implements INetworkData {

    private int lastValue;
    private final IntSupplier getter;
    private final IntConsumer setter;
    private final int id;

    public NetworkInt(int id, IntSupplier getter, IntConsumer setter) {
        this.id = id;
        this.getter = getter;
        this.setter = setter;
    }

    public Integer get() {
        return lastValue;
    }

    public void set(Object value) {
        setter.accept((Integer) value);
    }

    @Override
    public void update() {
        lastValue = getter.getAsInt();
    }

    @Override
    public boolean hasChanged() {
        int value = getter.getAsInt();
        if(lastValue != value) {
            lastValue = value;
            return true;
        }
        return false;
    }

    @Override
    public NetworkDataType getType() {
        return NetworkDataType.INT;
    }

    @Override
    public int getId() {
        return id;
    }
}
