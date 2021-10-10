package mrghastien.thermocraft.common.capabilities.heat.transport.cables;

import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.network.packets.UpdateCablePacket;
import mrghastien.thermocraft.common.tileentities.cables.HeatTransmitterTile;
import mrghastien.thermocraft.util.ModUtils;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConvectorCable extends Cable {

    protected Fluid fluid;

    public ConvectorCable(World world, BlockPos pos, HeatTransmitterTile<?> tileEntity) {
        super(world, pos, tileEntity);
    }

    public Fluid getFluid() {
        return fluid;
    }

    public void setFluid(Fluid fluid) {
        this.fluid = fluid;
        if(!world.isClientSide() && updateDirections()) HeatNetworkHandler.instance().sendCableChangesToClient(this, UpdateCablePacket.UpdateType.CONNECTIONS);
    }

    @Override
    public boolean canConnect(Direction dir) {
        boolean result = super.canConnect(dir);
        Cable c = ModUtils.getCable(pos.relative(dir), world);
        return result && (c instanceof Pump || ((ConvectorCable) c).getFluid() == getFluid());
    }

    @Override
    public HeatNetworkHandler.HeatNetworkType getType() {
        return HeatNetworkHandler.HeatNetworkType.CONVECTOR;
    }
}
