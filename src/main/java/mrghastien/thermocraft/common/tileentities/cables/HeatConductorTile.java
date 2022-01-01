package mrghastien.thermocraft.common.tileentities.cables;

import mrghastien.thermocraft.common.capabilities.heat.transport.cables.ConductorCable;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class HeatConductorTile extends HeatTransmitterTile<ConductorCable> {

    public HeatConductorTile(BlockPos pos, BlockState state) {
        super(ModTileEntities.HEAT_CONDUCTOR.get(), pos, state);
    }

    @Override
    protected ConductorCable createCable() {
        return new ConductorCable(level, worldPosition, this);
    }
}
