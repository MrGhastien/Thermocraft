package mrghastien.thermocraft.common.capabilities.fluid;

import mrghastien.thermocraft.api.fluid.IModFluidTank;
import mrghastien.thermocraft.common.network.NetworkDataType;
import mrghastien.thermocraft.common.network.NetworkHandler;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ModFluidHandler implements IDelegateFluidHandler {

    private final IModFluidTank[] tanks;
    private final LazyOptional<ModFluidHandler> lazy = LazyOptional.of(() -> this);

    public ModFluidHandler(IModFluidTank... tanks) {
        this.tanks = Arrays.copyOf(tanks, tanks.length);
    }
    @Override
    public int getTanks() {
        return tanks.length;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return getTank(tank).getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return getTank(tank).getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return getTank(tank).isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
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
    public FluidStack drain(FluidStack resource, FluidAction action) {
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
    public FluidStack drain(int maxDrain, FluidAction action) {
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

    public int getAvailableSpace() {
        int amount = 0;
        for(IModFluidTank tank : tanks) {
            amount += tank.getAvailableSpace();
        }
        return amount;
    }

    public boolean isEmpty() {
        for(IModFluidTank tank : tanks) {
            if(!tank.isEmpty()) return false;
        }
        return true;
    }

    public boolean isFull() {
        for(IModFluidTank tank : tanks) {
            if(!tank.isFull()) return false;
        }
        return true;
    }

    public boolean contains(FluidStack fluid) {
        int amount = 0;
        for(IModFluidTank tank : tanks) {
            if(tank.getFluid().isFluidEqual(fluid)) amount += tank.getFluidAmount();
        }
        return amount >= fluid.getAmount();
    }

    @Override
    public List<IFluidTank> getFluidTanks() {
        return Arrays.asList(tanks);
    }

    public IModFluidTank getTank(int index) {
        if(index < 0) throw new ArrayIndexOutOfBoundsException("Index cannot be negative !");
        else if(index < tanks.length)
            return tanks[index];
        else throw new ArrayIndexOutOfBoundsException("Index of tank doesn't represent any tank ! (index too large)");
    }

    public LazyOptional<ModFluidHandler> getLazy() {
        return lazy;
    }

    public void gatherData(Object key, PacketDistributor.PacketTarget target, World world) {
        NetworkHandler handler = NetworkHandler.getInstance(world);
        for(IModFluidTank tank : tanks) {
            handler.add(NetworkDataType.FLUIDSTACK, target, key, tank::getFluid, v -> tank.setFluid((FluidStack) v));
            handler.add(NetworkDataType.INT, target, key, tank::getCapacity, v -> tank.setCapacity((int) v));
        }
    }
}
