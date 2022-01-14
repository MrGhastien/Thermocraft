package mrghastien.thermocraft.common.capabilities.tartanicflux;

import mrghastien.thermocraft.api.capabilities.tartanicflux.ITartanicFluxHandler;
import mrghastien.thermocraft.util.math.FixedPointNumber;
import net.minecraftforge.common.util.LazyOptional;

public class TartanicFluxHandler implements ITartanicFluxHandler {

    private final FixedPointNumber.Mutable flux;
    private FixedPointNumber capacity, leakageCapacity;
    private boolean canReceive, canExtract;
    private boolean canLeak;
    private final LazyOptional<TartanicFluxHandler> lazy;

    public TartanicFluxHandler(FixedPointNumber flux, FixedPointNumber capacity, FixedPointNumber leakageCapacity, boolean canReceive, boolean canExtract, boolean canLeak) {
        this.flux = flux.toMutable();
        this.capacity = capacity;
        this.leakageCapacity = leakageCapacity;
        this.canReceive = canReceive;
        this.canExtract = canExtract;
        this.canLeak = canLeak;
        this.lazy = LazyOptional.of(() -> this);
    }

    public TartanicFluxHandler(long capacity, long leakageCapacity, boolean canReceive, boolean canExtract, boolean canLeak) {
        this(FixedPointNumber.ZERO, FixedPointNumber.valueOf(capacity), FixedPointNumber.valueOf(leakageCapacity), canReceive, canExtract, canLeak);
    }

    @Override
    public FixedPointNumber getFlux() {
        return flux.toImmutable();
    }

    @Override
    public FixedPointNumber getCapacity() {
        return capacity;
    }

    @Override
    public FixedPointNumber getLeakageCapacity() {
        return leakageCapacity;
    }

    @Override
    public FixedPointNumber transferFlux(FixedPointNumber flux, boolean simulate) {
        if(flux.isNegative()) {
            flux = flux.negate();
            if (!canExtract())
                return FixedPointNumber.ZERO;

            FixedPointNumber extracted = FixedPointNumber.min(this.flux, flux);
            if (!simulate)
                this.flux.sub(extracted);
            return extracted;
        }

        if(!canReceive())
            return FixedPointNumber.ZERO;

        FixedPointNumber received = FixedPointNumber.min(capacity.sub(this.flux), flux);
        if(!simulate)
            this.flux.add(received);
        return received;
    }

    @Override
    public boolean canReceive() {
        return canReceive;
    }

    @Override
    public boolean canExtract() {
        return canExtract;
    }

    @Override
    public boolean canLeak() {
        return canLeak;
    }

    public void setCapacity(FixedPointNumber capacity) {
        this.capacity = capacity;
    }

    public void setLeakageCapacity(FixedPointNumber leakageCapacity) {
        this.leakageCapacity = leakageCapacity;
    }

    public void setCanReceive(boolean canReceive) {
        this.canReceive = canReceive;
    }

    public void setCanExtract(boolean canExtract) {
        this.canExtract = canExtract;
    }

    public void setCanLeak(boolean canLeak) {
        this.canLeak = canLeak;
    }

    public LazyOptional<TartanicFluxHandler> lazy() {
        return lazy;
    }
}
