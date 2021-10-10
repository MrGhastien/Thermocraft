package mrghastien.thermocraft.common.capabilities.heat.transport.cables;

import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.tileentities.cables.HeatTransmitterTile;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConductorCable extends Cable{

    public ConductorCable(World world, BlockPos pos, HeatTransmitterTile<?> tileEntity) {
        super(world, pos, tileEntity);
    }

    @Override
    public HeatNetworkHandler.HeatNetworkType getType() {
        return HeatNetworkHandler.HeatNetworkType.CONDUCTOR;
    }
}