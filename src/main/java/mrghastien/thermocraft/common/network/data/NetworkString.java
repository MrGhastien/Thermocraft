package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.network.NetworkDataType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkString implements INetworkData {

    private String lastValue;
    private final Supplier<String> getter;
    private final Consumer<String> setter;
    private final int id;

    public NetworkString(int id, Supplier<String> getter, Consumer<String> setter) {
        this.id = id;
        this.getter = getter;
        this.setter = setter;
    }

    public String get() {
        return lastValue;
    }

    public void set(Object value) {
        setter.accept((String) value);
    }

    @Override
    public void update() {
        lastValue = getter.get();
    }

    @Override
    public boolean hasChanged() {
        String value = getter.get();
        if(lastValue == null && value != null) return true;
        if(lastValue != null && !lastValue.equals(value)) {
            lastValue = value;
            return true;
        }
        return false;
    }

    @Override
    public NetworkDataType getType() {
        return NetworkDataType.STRING;
    }

    @Override
    public int getId() {
        return id;
    }
}
