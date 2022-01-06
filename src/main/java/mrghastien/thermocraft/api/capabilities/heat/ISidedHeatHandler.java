package mrghastien.thermocraft.api.capabilities.heat;

import mrghastien.thermocraft.util.math.FixedPointNumber;
import net.minecraft.core.Direction;

public interface ISidedHeatHandler extends IHeatHandler {

    TransferType getTransferType(Direction dir);

    void transferEnergy(Direction dir, long energy);

    void transferEnergy(Direction dir, FixedPointNumber energy);
}
