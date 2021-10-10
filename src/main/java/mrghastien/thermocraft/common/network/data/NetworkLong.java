package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.network.NetworkDataType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkLong implements INetworkData {

    private long lastValue;
    private final Supplier<Long> getter;
    private final Consumer<Long> setter;
    private final int id;

    public NetworkLong(int id, Supplier<Long> getter, Consumer<Long> setter) {
        this.id = id;
        this.getter = getter;
        this.setter = setter;
    }

    public Long get() {
        return lastValue;
    }

    public void set(Object value) {
        setter.accept((Long) value);
    }

    @Override
    public void update() {
        lastValue = getter.get();
    }

    @Override
    public boolean hasChanged() {
        long value = getter.get();
        if(lastValue != value) {
            lastValue = value;
            return true;
        }
        return false;
    }

    @Override
    public NetworkDataType getType() {
        return NetworkDataType.LONG;
    }

    @Override
    public int getId() {
        return id;
    }
}
