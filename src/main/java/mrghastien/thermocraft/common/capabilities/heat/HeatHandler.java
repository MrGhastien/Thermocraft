package mrghastien.thermocraft.common.capabilities.heat;

import mrghastien.thermocraft.api.IChangeListener;
import mrghastien.thermocraft.api.heat.IHeatHandler;
import mrghastien.thermocraft.common.network.NetworkDataType;
import mrghastien.thermocraft.common.network.NetworkHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

public class HeatHandler implements IHeatHandler {

    protected double heatCapacity;
    protected long internalEnergy;
    protected double conductionCoefficient;
    protected double insulationCoefficient;

    private final LazyOptional<IHeatHandler> lazy;
    private final IChangeListener changeListener;

    public HeatHandler(double heatCapacity, long internalEnergy, double conductionCoefficient, double insulationCoefficient) {
        this(heatCapacity, internalEnergy, conductionCoefficient, insulationCoefficient, () -> {});
    }

    public HeatHandler(double heatCapacity, double conductionCoefficient, double insulationCoefficient, @Nullable IChangeListener listener) {
        this(heatCapacity, (long) (heatCapacity * IHeatHandler.AIR_TEMPERATURE), conductionCoefficient, insulationCoefficient, listener);
    }

    public HeatHandler(double heatCapacity, long internalEnergy, double conductionCoefficient, double insulationCoefficient, @Nullable IChangeListener listener) {
        this.heatCapacity = heatCapacity;
        this.internalEnergy = internalEnergy;
        this.changeListener = listener;
        this.conductionCoefficient = conductionCoefficient * 0.05; //Multiplying by 0.05 because 1 tick is 0.05 second
        this.insulationCoefficient = insulationCoefficient * 0.05;
        this.lazy = LazyOptional.of(() -> this);
    }

    @Override
    public double getTemperature() {
        return getInternalEnergy() / getHeatCapacity();
    }

    @Override
    public long getInternalEnergy() {
        return internalEnergy;
    }

    @Override
    public void setTemperature(double temperature) {
        setInternalEnergy((long) (temperature * getHeatCapacity()));
    }

    @Override
    public void setInternalEnergy(long energy) {
        if(energy >= 0) this.internalEnergy = energy;
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
            setInternalEnergy((long) (getInternalEnergy() + (capacity - getHeatCapacity()) * IHeatHandler.AIR_TEMPERATURE));
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
        setInternalEnergy(getInternalEnergy() + energy);
    }

    public void transferEnergy(double energy) {
        transferEnergy((long)energy);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("internalEnergy", getInternalEnergy());
        nbt.putDouble("heatCapacity", getHeatCapacity());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
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

    public void gatherData(Object key, PacketDistributor.PacketTarget target, World world) {
        NetworkHandler handler = NetworkHandler.getInstance(world);
        handler.add(NetworkDataType.DOUBLE, target, key, this::getHeatCapacity, v -> this.setHeatCapacity((double) v, true));
        handler.add(NetworkDataType.LONG, target, key, this::getInternalEnergy, v -> this.setInternalEnergy((long) v));
        handler.add(NetworkDataType.DOUBLE, target, key, this::getConductionCoefficient, v -> this.setConductionCoefficient((double) v));
        handler.add(NetworkDataType.DOUBLE, target, key, this::getInsulationCoefficient, v -> this.setInsulationCoefficient((double) v));
    }
}
