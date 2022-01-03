package mrghastien.thermocraft.common.capabilities.tartanicflux;

import mrghastien.thermocraft.util.math.FixedPointNumber;

public class TartanicFluxHandler implements ITartanicFluxHandler {

    private final FixedPointNumber.Mutable flux;
    private FixedPointNumber capacity, leakageCapacity;
    private boolean canReceive, canExtract;
    private boolean canLeak;

    public TartanicFluxHandler(FixedPointNumber flux, FixedPointNumber capacity, FixedPointNumber leakageCapacity, boolean canReceive, boolean canExtract, boolean canLeak) {
        this.flux = flux.toMutable();
        this.capacity = capacity;
        this.leakageCapacity = leakageCapacity;
        this.canReceive = canReceive;
        this.canExtract = canExtract;
        this.canLeak = canLeak;
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
    public void transferFlux(FixedPointNumber flux) {
        this.flux.add(flux);
    }

    @Override
    public void transferFlux(long flux) {
        this.flux.add(flux);
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
}
