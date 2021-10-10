package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.network.NetworkDataType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkFloat implements INetworkData {

    private float lastValue;
    private final Supplier<Float> getter;
    private final Consumer<Float> setter;
    private final int id;

    public NetworkFloat(int id, Supplier<Float> getter, Consumer<Float> setter) {
        this.id = id;
        this.getter = getter;
        this.setter = setter;
    }

    public Float get() {
        return lastValue;
    }

    public void set(Object value) {
        setter.accept((Float) value);
    }

    @Override
    public void update() {
        lastValue = getter.get();
    }

    @Override
    public boolean hasChanged() {
        float value = getter.get();
        if(lastValue != value) {
            lastValue = value;
            return true;
        }
        return false;
    }

    @Override
    public NetworkDataType getType() {
        return NetworkDataType.FLOAT;
    }

    @Override
    public int getId() {
        return id;
    }
}
