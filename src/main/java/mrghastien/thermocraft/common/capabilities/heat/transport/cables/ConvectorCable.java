package mrghastien.thermocraft.common.capabilities.heat.transport.cables;

import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.blocks.transmitters.HeatTransmitterBlockEntity;
import mrghastien.thermocraft.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ConvectorCable extends Cable {

    protected Fluid fluid;

    public ConvectorCable(Level world, BlockPos pos, HeatTransmitterBlockEntity<?> tileEntity) {
        super(world, pos, tileEntity);
    }

    public Fluid getFluid() {
        return fluid;
    }

    public void setFluid(Fluid fluid) {
        this.fluid = fluid;
        if(!world.isClientSide() && updateDirections())
            world.sendBlockUpdated(pos, getBlockState(), getBlockState(), Block.UPDATE_ALL);
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
