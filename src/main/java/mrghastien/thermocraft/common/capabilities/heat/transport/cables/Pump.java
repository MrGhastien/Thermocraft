package mrghastien.thermocraft.common.capabilities.heat.transport.cables;

import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.tileentities.cables.HeatTransmitterTile;
import mrghastien.thermocraft.util.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Pump extends Cable {

    Direction facing;

    public Pump(World world, BlockPos pos, HeatTransmitterTile<?> tileEntity) {
        super(world, pos, tileEntity);
    }

    @Override
    public boolean isPump() {
        return true;
    }

    @Override
    public boolean isIntersection() {
        return false;
    }

    @Override
    public HeatNetworkHandler.HeatNetworkType getType() {
        return HeatNetworkHandler.HeatNetworkType.CONVECTOR;
    }

    @Override
    public boolean updateDirections() {
        BlockState state = getBlockState();
        if(!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return false;
        this.facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction clockWise = facing.getClockWise();
        Direction counterClockWise = facing.getCounterClockWise();
        boolean changed = changeConnection(clockWise, ModUtils.getCable(pos.relative(clockWise), world) == null ? TransferType.NONE : TransferType.NEUTRAL);
        changed |= changeConnection(counterClockWise, ModUtils.getCable(pos.relative(counterClockWise), world) == null ? TransferType.NONE : TransferType.NEUTRAL);
        return changed;
    }
}