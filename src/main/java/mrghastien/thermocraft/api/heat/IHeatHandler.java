package mrghastien.thermocraft.api.heat;

import mrghastien.thermocraft.api.IChangeListener;
import mrghastien.thermocraft.util.math.FixedPointNumber;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IHeatHandler extends INBTSerializable<CompoundTag>, IChangeListener {

    double AIR_TEMPERATURE = 293.0;

    double getTemperature();

    FixedPointNumber getInternalEnergy();

    void setTemperature(double temperature);

    void setInternalEnergy(long energy);

    void setInternalEnergy(FixedPointNumber energy);

    /**
     * The heat capacity of an object represents the amount of energy per temperature degree
     * (i.e. an object with a heat capacity of 5 needs 5 energy units to gain 1 degree of temperature).
     * @return The heat capacity of this handler.
     */
    double getHeatCapacity();

    void setHeatCapacity(double capacity, boolean updateEnergy);

    /**
     * The conduction coefficient represents the amount of heat transferred to another object in contact with it.
     * The greater the coefficient, the more energy is transferred per unit of tick
     */
    double getConductionCoefficient();

    void setConductionCoefficient(double conductionCoefficient);

    /**
     * The insulation coefficient represents the inverse of the amount of heat dissipated.
     * The greater, the more energy is dissipated in the environment.
     */
    double getInsulationCoefficient();

    void setInsulationCoefficient(double insulationCoefficient);

    default double getDissipation() {
        return getInsulationCoefficient() * (IHeatHandler.AIR_TEMPERATURE - getTemperature());
    }

    /**
     *
     * @param energy The energy transferred to the handler (can be negative)
     */
    void transferEnergy(long energy);

    void transferEnergy(FixedPointNumber energy);

    boolean canReceive();

    boolean canExtract();
}
