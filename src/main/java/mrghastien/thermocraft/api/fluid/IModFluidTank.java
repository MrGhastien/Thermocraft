package mrghastien.thermocraft.api.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public interface IModFluidTank extends IFluidTank, INBTSerializable<CompoundTag> {

    void setFluid(FluidStack stack);

    void setAmount(int amount);

    void setCapacity(int capacity);

    boolean isEmpty();

    boolean isFull();

    int getAvailableSpace();
}