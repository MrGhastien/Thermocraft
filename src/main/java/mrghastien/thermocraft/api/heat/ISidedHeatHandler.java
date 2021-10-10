package mrghastien.thermocraft.api.heat;

import net.minecraft.util.Direction;

public interface ISidedHeatHandler extends IHeatHandler {

    TransferType getTransferType(Direction dir);

    void transferEnergy(Direction dir, long energy);
}
