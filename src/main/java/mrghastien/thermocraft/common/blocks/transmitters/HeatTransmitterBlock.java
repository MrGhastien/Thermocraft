package mrghastien.thermocraft.common.blocks.transmitters;

import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.blocks.transmitters.HeatTransmitterTile;
import mrghastien.thermocraft.util.Constants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;

public abstract class HeatTransmitterBlock extends Block implements EntityBlock {

    public static final VoxelShape CENTER_BOX = Block.box(5, 5, 5, 11, 11, 11);
    public static final VoxelShape ARM_DOWN = Block.box(5, 0, 5, 11, 5, 11);
    public static final VoxelShape ARM_UP = Block.box(5, 11, 5, 11, 16, 11);
    public static final VoxelShape ARM_NORTH = Block.box(5, 5, 0, 11, 11, 5);
    public static final VoxelShape ARM_SOUTH = Block.box(5, 5, 11, 11, 11, 16);
    public static final VoxelShape ARM_EAST = Block.box(11, 5, 5, 16, 11, 11);
    public static final VoxelShape ARM_WEST = Block.box(0, 5, 5, 5, 11, 11);

    public static final EnumMap<Direction, VoxelShape> VOXEL_MAP = Util.make(new EnumMap<>(Direction.class), map -> {
        map.put(Direction.NORTH, ARM_NORTH);
        map.put(Direction.EAST, ARM_EAST);
        map.put(Direction.SOUTH, ARM_SOUTH);
        map.put(Direction.WEST, ARM_WEST);
        map.put(Direction.UP, ARM_UP);
        map.put(Direction.DOWN, ARM_DOWN);
    });

    public HeatTransmitterBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    public abstract HeatNetworkHandler.HeatNetworkType getNetworkType();

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        VoxelShape shape = CENTER_BOX;
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof HeatTransmitterTile<?>) {
            EnumMap<Direction, TransferType> connections = ((HeatTransmitterTile<?>) te).getCable().getConnections();
            for (Direction dir : Constants.DIRECTIONS) {
                TransferType type = connections.get(dir);
                if (type != TransferType.NONE)
                    shape = Shapes.or(shape, VOXEL_MAP.get(dir));
            }
        }
        return shape;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, neighborBlock, neighborPos, isMoving);
        if(world.isClientSide()) return;
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof HeatTransmitterTile<?>) {
            Direction dir = Direction.getNearest(neighborPos.getX() - pos.getX(),
                    neighborPos.getY() - pos.getY(),
                    neighborPos.getZ() - pos.getZ());
            ((HeatTransmitterTile<?>)te).OnNeighborChanged(dir);
        }

    }
}
