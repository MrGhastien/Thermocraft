package mrghastien.thermocraft.common.tileentities.cables;

import mrghastien.thermocraft.common.capabilities.heat.transport.cables.ConductorCable;
import mrghastien.thermocraft.common.registries.ModTileEntities;

public class HeatConductorTile extends HeatTransmitterTile<ConductorCable> {

    public HeatConductorTile() {
        super(ModTileEntities.HEAT_CONDUCTOR.get());
    }

    @Override
    protected ConductorCable createCable() {
        return new ConductorCable(level, worldPosition, this);
    }
}
