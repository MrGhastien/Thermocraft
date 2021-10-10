package mrghastien.thermocraft.common.capabilities.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;

public interface IDelegateFluidHandler extends IFluidHandler {

    List<IFluidTank> getFluidTanks();

    default IFluidTank getTank(int i) {
        return getFluidTanks().get(i);
    }

    @Override
    default int getTanks() {
        return getFluidTanks().size();
    }

    @Override
    default int getTankCapacity(int tank) {
        return getTank(tank).getCapacity();
    }

    @Nonnull
    @Override
    default FluidStack getFluidInTank(int tank) {
        return getTank(tank).getFluid();
    }

    @Override
    default boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return getTank(tank).isFluidValid(stack);
    }

    @Override
    default int fill(FluidStack resource, FluidAction action) {
        FluidStack copy = resource.copy();
        int filled = 0;
        for(int i = 0; i < getTanks(); i++) {
            IFluidTank tank = getTank(i);
            if(tank.isFluidValid(copy)) {
                filled += tank.fill(copy, action);
                copy.shrink(filled);
            }
            if(copy.getAmount() == 0) break;
        }
        return filled;
    }

    @Nonnull
    @Override
    default FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack copy = resource.copy();
        for(int i = 0; i < getTanks(); i++) {
            IFluidTank tank = getTank(i);
            if(tank.getFluid().isFluidEqual(resource)) {
                copy.grow(tank.drain(copy, action).getAmount());
            }
            if(copy.getAmount() >= resource.getAmount()) break;
        }
        return new FluidStack(resource, resource.getAmount() - copy.getAmount());
    }

    @Nonnull
    @Override
    default FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack fluid = FluidStack.EMPTY;
        for(int i = 0; i < getTanks(); i++) {
            IFluidTank tank = getTank(i);
            if(fluid == FluidStack.EMPTY && tank.getFluid() != FluidStack.EMPTY) {
                fluid = tank.drain(maxDrain, action);
                maxDrain -= fluid.getAmount();
            } else if(tank.getFluid().isFluidEqual(fluid)){
                fluid.grow(tank.drain(maxDrain, action).getAmount());
            }
        }
        return fluid;
    }
}
