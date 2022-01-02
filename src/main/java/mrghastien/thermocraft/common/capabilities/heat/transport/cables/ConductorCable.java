package mrghastien.thermocraft.common.capabilities.heat.transport.cables;


import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.blocks.transmitters.HeatTransmitterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ConductorCable extends Cable{

    public ConductorCable(Level world, BlockPos pos, HeatTransmitterBlockEntity<?> tileEntity) {
        super(world, pos, tileEntity);
    }

    @Override
    public HeatNetworkHandler.HeatNetworkType getType() {
        return HeatNetworkHandler.HeatNetworkType.CONDUCTOR;
    }
}
