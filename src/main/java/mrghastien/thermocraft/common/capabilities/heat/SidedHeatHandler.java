package mrghastien.thermocraft.common.capabilities.heat;

import mrghastien.thermocraft.api.IChangeListener;
import mrghastien.thermocraft.api.heat.IHeatHandler;
import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.util.Constants;
import mrghastien.thermocraft.util.math.FixedPointNumber;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.function.Function;

public class SidedHeatHandler extends HeatHandler implements mrghastien.thermocraft.api.heat.ISidedHeatHandler {

    private final Function<Direction, TransferType> transferTypeFunction;
    private final EnumMap<Direction, LazyOptional<Interface>> interfaces = new EnumMap<>(Direction.class);

    public SidedHeatHandler(double heatCapacity, long internalEnergy, double conductionCoefficient, double insulationCoefficient) {
        this(heatCapacity, FixedPointNumber.valueOf(internalEnergy), conductionCoefficient, insulationCoefficient, null, d -> TransferType.BOTH);
    }

    public SidedHeatHandler(double heatCapacity, double conductionCoefficient, double insulationCoefficient, @Nullable IChangeListener listener, Function<Direction, TransferType> transferTypeFunction) {
        this(heatCapacity, FixedPointNumber.valueOf(heatCapacity * IHeatHandler.AIR_TEMPERATURE), conductionCoefficient, insulationCoefficient, listener, transferTypeFunction);
    }

    public SidedHeatHandler(double heatCapacity, FixedPointNumber internalEnergy, double conductionCoefficient, double insulationCoefficient, @Nullable IChangeListener listener, Function<Direction, TransferType> transferTypeFunction) {
        super(heatCapacity, internalEnergy, conductionCoefficient, insulationCoefficient, listener);
        this.transferTypeFunction = transferTypeFunction;
        for(Direction dir : Constants.DIRECTIONS) {
            interfaces.put(dir, LazyOptional.of(() -> new Interface(dir)));
        }
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    public LazyOptional<Interface> getLazy(Direction dir) {
        return interfaces.get(dir);
    }

    @Override
    public TransferType getTransferType(Direction dir) {
        if(dir == null) return TransferType.BOTH;
        return transferTypeFunction.apply(dir);
    }

    @Override
    public void transferEnergy(Direction dir, long energy) {
        TransferType type = getTransferType(dir);
        if(type.canReceive() && energy > 0 || type.canExtract() && energy < 0)
            transferEnergy(energy);
    }

    @Override
    public void transferEnergy(Direction dir, FixedPointNumber energy) {
        TransferType type = getTransferType(dir);
        if(type.canReceive() && energy.isGreaterThan(0) || type.canExtract() && energy.isLessThan(0))
            transferEnergy(energy);
    }

    public class Interface implements IHeatHandler {

        private final TransferType type;

        public Interface(Direction dir) {
            this.type = getTransferType(dir);
        }

        @Override
        public void onChanged() {
            SidedHeatHandler.this.onChanged();
        }

        @Override
        public double getTemperature() {
            return SidedHeatHandler.this.getTemperature();
        }

        @Override
        public FixedPointNumber getInternalEnergy() {
            return SidedHeatHandler.this.getInternalEnergy();
        }

        @Override
        public void setTemperature(double temperature) {
            SidedHeatHandler.this.setTemperature(temperature);
        }

        @Override
        public void setInternalEnergy(long energy) {
            SidedHeatHandler.this.setInternalEnergy(energy);
        }

        @Override
        public void setInternalEnergy(FixedPointNumber energy) {
            SidedHeatHandler.this.setInternalEnergy(energy);
        }

        @Override
        public double getHeatCapacity() {
            return SidedHeatHandler.this.getHeatCapacity();
        }

        @Override
        public void setHeatCapacity(double capacity, boolean updateEnergy) {
            SidedHeatHandler.this.setHeatCapacity(capacity, updateEnergy);
        }

        @Override
        public double getConductionCoefficient() {
            return SidedHeatHandler.this.getConductionCoefficient();
        }

        @Override
        public void setConductionCoefficient(double conductionCoefficient) {
            SidedHeatHandler.this.setConductionCoefficient(conductionCoefficient);
        }

        @Override
        public double getInsulationCoefficient() {
            return SidedHeatHandler.this.getInsulationCoefficient();
        }

        @Override
        public void setInsulationCoefficient(double insulationCoefficient) {
            SidedHeatHandler.this.setInsulationCoefficient(insulationCoefficient);
        }

        @Override
        public void transferEnergy(long energy) {
            if(energy > 0 && type.canReceive() || energy < 0 && type.canExtract())
                SidedHeatHandler.this.transferEnergy(energy);
        }

        @Override
        public void transferEnergy(FixedPointNumber energy) {
            if(energy.isGreaterThan(0) && type.canReceive() || energy.isLessThan(0) && type.canExtract())
                SidedHeatHandler.this.transferEnergy(energy);
        }

        @Override
        public boolean canReceive() {
            return type.canReceive();
        }

        @Override
        public boolean canExtract() {
            return type.canExtract();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return SidedHeatHandler.this.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            SidedHeatHandler.this.deserializeNBT(nbt);
        }


    }
}
