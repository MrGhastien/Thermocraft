package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.network.NetworkDataType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkDouble implements INetworkData {

    private double lastValue;
    private final Supplier<Double> getter;
    private final Consumer<Double> setter;
    private final int id;

    public NetworkDouble(int id, Supplier<Double> getter, Consumer<Double> setter) {
        this.id = id;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public Double get() {
        return lastValue;
    }

    @Override
    public void set(Object value) {
        setter.accept((Double) value);
    }

    @Override
    public void update() {
        lastValue = getter.get();
    }

    @Override
    public boolean hasChanged() {
        double value = getter.get();
        if(lastValue != value) {
            lastValue = value;
            return true;
        }
        return false;
    }

    @Override
    public NetworkDataType getType() {
        return NetworkDataType.DOUBLE;
    }

    @Override
    public int getId() {
        return id;
    }
}
