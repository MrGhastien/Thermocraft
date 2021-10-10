package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.network.NetworkDataType;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkCollection implements INetworkData {

    private List<Object> lastValue;
    private final Supplier<List<Object>> getter;
    private final Function<Integer, Object> elementGetter;
    private final BiConsumer<Integer, Object> elementSetter;
    private final NetworkDataType type;
    private final int id;

    public NetworkCollection(NetworkDataType type, Supplier<List<Object>> getter, Function<Integer, Object> elementGetter, BiConsumer<Integer, Object> elementSetter, int id) {
        this.type = type;
        this.getter = getter;
        this.elementGetter = elementGetter;
        this.elementSetter = elementSetter;
        this.id = id;
    }

    @Override
    public void update() {
        lastValue = getter.get();
    }

    @Override
    public boolean hasChanged() {
        List<Object> value = getter.get();
        if(!Objects.equals(lastValue, value)) {
            lastValue = value;
            return true;
        }
        return false;
    }

    @Override
    public Object get() {
        return lastValue;
    }

    public Object get(int id) {
        return elementGetter.apply(id);
    }

    //Shouldn't be used
    @Override
    public void set(Object value) {
        throw new UnsupportedOperationException();
    }

    public void set(int id, Object value) {
        elementSetter.accept(id, value);
    }

    @Override
    public NetworkDataType getType() {
        return NetworkDataType.LIST;
    }

    @Override
    public int getId() {
        return id;
    }
}
