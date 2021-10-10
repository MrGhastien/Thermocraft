package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.network.NetworkDataType;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkItemStack implements INetworkData {

    private ItemStack lastValue;
    private final Supplier<ItemStack> getter;
    private final Consumer<ItemStack> setter;
    private final int id;

    public NetworkItemStack(int id, Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
        this.id = id;
        this.getter = getter;
        this.setter = setter;
    }

    public ItemStack get() {
        return lastValue;
    }

    public void set(Object value) {
        setter.accept((ItemStack) value);
    }

    @Override
    public void update() {
        lastValue = getter.get();
    }

    @Override
    public boolean hasChanged() {
        ItemStack value = getter.get();
        if(lastValue == null && value != null) return true;
        if(lastValue != null && !ItemStack.matches(lastValue, value)) {
            lastValue = value;
            return true;
        }
        return false;
    }

    @Override
    public NetworkDataType getType() {
        return NetworkDataType.ITEMSTACK;
    }

    @Override
    public int getId() {
        return id;
    }
}
