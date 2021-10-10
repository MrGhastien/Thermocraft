package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.network.NetworkDataType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkBoolean implements INetworkData {

    private boolean lastValue;
    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;
    private final int id;

    public NetworkBoolean(int id, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        this.id = id;
        this.getter = getter;
        this.setter = setter;
    }

    public Boolean get() {
        return lastValue;
    }

    public void set(Object value) {
        setter.accept((Boolean) value);
    }

    @Override
    public void update() {
        lastValue = getter.get();
    }

    @Override
    public boolean hasChanged() {
        boolean value = getter.get();
        if(lastValue != value) {
            lastValue = value;
            return true;
        }
        return false;
    }

    @Override
    public NetworkDataType getType() {
        return NetworkDataType.BOOLEAN;
    }

    @Override
    public int getId() {
        return id;
    }

}
