package mrghastien.thermocraft.common.capabilities.fluid;

import mrghastien.thermocraft.api.IChangeListener;
import mrghastien.thermocraft.api.fluid.IModFluidTank;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.network.data.DataType;
import mrghastien.thermocraft.common.network.data.IDataHolder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * Basically the same as {@link net.minecraftforge.fluids.capability.templates.FluidTank}, but does not implement {@link IFluidHandler}
 */
public class ModFluidTank implements IModFluidTank {

    private final Predicate<FluidStack> validator;
    private final IChangeListener listener;

    private FluidStack fluid = FluidStack.EMPTY;
    private int capacity;

    public ModFluidTank(int capacity) {
        this(capacity, stack -> true, () -> {});
    }

    public ModFluidTank(int capacity, Predicate<FluidStack> validator, IChangeListener listener) {
        this.capacity = capacity;
        this.validator = validator;
        this.listener = listener;
    }

    public ModFluidTank(int capacity, IChangeListener listener) {
        this(capacity, stack -> true, listener);
    }

    public ModFluidTank(int capacity, Predicate<FluidStack> validator) {
        this(capacity, validator, () -> {});
    }

    @Override
    public void setFluid(FluidStack stack) {
        if(stack.isFluidEqual(fluid))
            setAmount(stack.getAmount());
        else
            this.fluid = stack;
    }

    @Override
    public void setAmount(int amount) {
        this.fluid.setAmount(amount);
    }

    @Override
    public boolean isEmpty() {
        return fluid.isEmpty();
    }

    @Override
    public boolean isFull() {
        return fluid.getAmount() >= capacity;
    }

    @Override
    public int getAvailableSpace() {
        //The amount of the fluid stack isn't capped, so it can be greater than the capacity
        return Math.max(0, getCapacity() - getFluidAmount());
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        fluid.writeToNBT(nbt);
        nbt.putInt("Capacity", capacity);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        setFluid(FluidStack.loadFluidStackFromNBT(nbt));
        this.capacity = nbt.getInt("Capacity");
    }

    @Nonnull
    @Override
    public FluidStack getFluid() {
        return fluid;
    }

    @Override
    public int getFluidAmount() {
        return Math.min(capacity, fluid.getAmount());
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = Math.max(0, capacity);
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if(resource.isEmpty() || !isFluidValid(resource))
            return 0;
        if(!fluid.isEmpty() && !fluid.isFluidEqual(resource))
            return 0;

        if(action.simulate()) {
            if(fluid.isEmpty()) return Math.min(capacity, resource.getAmount()); //Return the amount of 'resource', or capacity if too high
            return Math.min(capacity - fluid.getAmount(), resource.getAmount()); //Else return the amount of 'resource', or the remaining space if too high
        }
        if(fluid.isEmpty()) {
            setFluid(new FluidStack(resource, Math.min(capacity, resource.getAmount())));
            onChanged();
            return fluid.getAmount();
        }

        int filled = getAvailableSpace();
        if(resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            setAmount(capacity);
        }
        if(filled > 0) onChanged();
        return filled;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        int drained = Math.min(fluid.getAmount(), maxDrain);
        FluidStack stack = new FluidStack(fluid, drained);
        if(action.execute() && drained > 0) {
            fluid.shrink(drained);
            onChanged();
        }
        return stack;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if(resource.isEmpty() || !resource.isFluidEqual(fluid)) return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }

    void onChanged() {
        listener.onChanged();
    }

    public void gatherData(String tankName, IDataHolder holder) {
        holder.addData(DataType.FLUID_STACK, tankName + "fluid", () -> getFluid().copy(), this::setFluid);
        holder.addData(DataType.INT, tankName + "capacity", this::getCapacity, this::setCapacity);
    }

    public void gatherData(IDataHolder holder) {
        gatherData("", holder);
    }
}
