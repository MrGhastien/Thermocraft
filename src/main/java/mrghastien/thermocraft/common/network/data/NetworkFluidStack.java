package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.network.NetworkDataType;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkFluidStack implements INetworkData {

    private FluidStack lastValue;
    private final Supplier<FluidStack> getter;
    private final Consumer<FluidStack> setter;
    private final int id;

    public NetworkFluidStack(int id, Supplier<FluidStack> getter, Consumer<FluidStack> setter) {
        this.id = id;
        this.getter = getter;
        this.setter = setter;
    }

    public FluidStack get() {
        return lastValue;
    }

    public void set(Object value) {
        setter.accept((FluidStack) value);
    }

    @Override
    public void update() {
        lastValue = getter.get().copy();
    }

    @Override
    public boolean hasChanged() {
        FluidStack value = getter.get().copy();
        if(lastValue == null && value != null) return true;
        if(lastValue != null && !lastValue.isFluidStackIdentical(value)) {
            lastValue = value;
            return true;
        }
        return false;
    }

    @Override
    public NetworkDataType getType() {
        return NetworkDataType.FLUIDSTACK;
    }
    @Override
    public int getId() {
        return id;
    }

}
