package mrghastien.thermocraft.common.capabilities.heat;

import mrghastien.thermocraft.api.IChangeListener;
import mrghastien.thermocraft.api.heat.IHeatHandler;
import mrghastien.thermocraft.common.network.data.DataType;
import mrghastien.thermocraft.common.network.data.IDataHolder;
import mrghastien.thermocraft.util.math.FixedPointNumber;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeatHandler implements IHeatHandler {

    protected double heatCapacity;
    protected FixedPointNumber.Mutable internalEnergy;
    protected double conductionCoefficient;
    protected double insulationCoefficient;

    private final LazyOptional<IHeatHandler> lazy;
    private final IChangeListener changeListener;

    public HeatHandler(double heatCapacity, long internalEnergy, double conductionCoefficient, double insulationCoefficient) {
        this(heatCapacity, FixedPointNumber.valueOf(internalEnergy), conductionCoefficient, insulationCoefficient, () -> {});
    }

    public HeatHandler(double heatCapacity, double conductionCoefficient, double insulationCoefficient, @Nullable IChangeListener listener) {
        this(heatCapacity, FixedPointNumber.valueOf(heatCapacity * IHeatHandler.AIR_TEMPERATURE), conductionCoefficient, insulationCoefficient, listener);
    }

    public HeatHandler(double heatCapacity, FixedPointNumber internalEnergy, double conductionCoefficient, double insulationCoefficient, @Nullable IChangeListener listener) {
        this.heatCapacity = heatCapacity;
        this.internalEnergy = internalEnergy.toMutable();
        this.changeListener = listener;
        this.conductionCoefficient = conductionCoefficient * 0.05; //Multiplying by 0.05 because 1 tick is 0.05 second
        this.insulationCoefficient = insulationCoefficient * 0.05;
        this.lazy = LazyOptional.of(() -> this);
    }

    @Override
    public double getTemperature() {
        return getInternalEnergy().doubleValue() / getHeatCapacity();
    }

    @Override
    public FixedPointNumber getInternalEnergy() {
        return internalEnergy.copy();
    }

    public long getInternalEnergyFloored() {
        return internalEnergy.longValue();
    }

    @Override
    public void setTemperature(double temperature) {
        setInternalEnergy((long) (temperature * getHeatCapacity()));
    }

    @Override
    public void setInternalEnergy(long energy) {
        if(energy >= 0) internalEnergy.set(energy, (short) 0);
    }

    @Override
    public void setInternalEnergy(FixedPointNumber internalEnergy) {
        this.internalEnergy.set(internalEnergy);
    }

    @Override
    public double getHeatCapacity() {
        return heatCapacity;
    }

    public void setHeatCapacity(double capacity) {
        setHeatCapacity(capacity, false);
    }

    @Override
    public void setHeatCapacity(double capacity, boolean updateEnergy) {
        if(updateEnergy) {
            setInternalEnergy((long) (getInternalEnergy().doubleValue() + (capacity - getHeatCapacity()) * IHeatHandler.AIR_TEMPERATURE));
        }
        this.heatCapacity = capacity;
    }

    @Override
    public double getConductionCoefficient() {
        return conductionCoefficient;
    }

    @Override
    public void setConductionCoefficient(double conductionCoefficient) {
        this.conductionCoefficient = conductionCoefficient;
    }

    @Override
    public double getInsulationCoefficient() {
        return insulationCoefficient;
    }

    @Override
    public void setInsulationCoefficient(double insulationCoefficient) {
        this.insulationCoefficient = insulationCoefficient;
    }

    public void ambient() {
        transferEnergy(getDissipation());
    }

    public void transferEnergy(long energy) {
        internalEnergy.add(energy);
        if(internalEnergy.isLessThan(0)) internalEnergy.set(0);
    }

    @Override
    public void transferEnergy(FixedPointNumber energy) {
        internalEnergy.add(energy);
        if(internalEnergy.isLessThan(0)) internalEnergy.set(0);

    }

    public void transferEnergy(double energy) {
        internalEnergy.add(energy);
        if(internalEnergy.isLessThan(0)) internalEnergy.set(0);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("internalEnergy", getInternalEnergyFloored());
        nbt.putDouble("heatCapacity", getHeatCapacity());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setInternalEnergy(nbt.getLong("internalEnergy"));
        setHeatCapacity(nbt.getDouble("heatCapacity"), false);
    }

    public LazyOptional<IHeatHandler> getLazy() {
        return lazy;
    }

    @Override
    public void onChanged() {
        changeListener.onChanged();
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    public void gatherData(String handlerName, @Nonnull IDataHolder holder) {
        holder.addData(DataType.DOUBLE, handlerName + "heat_capacity", this::getHeatCapacity, v -> this.setHeatCapacity(v, true));
        holder.addData(DataType.FIXED_POINT, handlerName + "internal_energy", this::getInternalEnergy, this::setInternalEnergy);
        holder.addData(DataType.DOUBLE, handlerName + "conduction", this::getConductionCoefficient, this::setConductionCoefficient);
        holder.addData(DataType.DOUBLE, handlerName + "insulation", this::getInsulationCoefficient, this::setInsulationCoefficient);
    }

    public void gatherData(@Nonnull IDataHolder holder) {
        gatherData("", holder);
    }
}
