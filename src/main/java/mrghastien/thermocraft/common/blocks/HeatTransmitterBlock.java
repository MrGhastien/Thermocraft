package mrghastien.thermocraft.common.blocks;

import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.tileentities.cables.HeatTransmitterTile;
import mrghastien.thermocraft.util.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumMap;

public abstract class HeatTransmitterBlock extends Block {

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

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    public abstract HeatNetworkHandler.HeatNetworkType getNetworkType();

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        VoxelShape shape = CENTER_BOX;
        TileEntity te = world.getBlockEntity(pos);
        if(te instanceof HeatTransmitterTile<?>) {
            EnumMap<Direction, TransferType> connections = ((HeatTransmitterTile<?>) te).getCable().getConnections();
            for (Direction dir : Constants.DIRECTIONS) {
                TransferType type = connections.get(dir);
                if (type != TransferType.NONE)
                    shape = VoxelShapes.or(shape, VOXEL_MAP.get(dir));
            }
        }
        return shape;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, neighborBlock, neighborPos, isMoving);
        if(world.isClientSide()) return;
        TileEntity te = world.getBlockEntity(pos);
        if(te instanceof HeatTransmitterTile<?>) {
            Direction dir = Direction.getNearest(neighborPos.getX() - pos.getX(),
                    neighborPos.getY() - pos.getY(),
                    neighborPos.getZ() - pos.getZ());
            ((HeatTransmitterTile<?>)te).OnNeighborChanged(dir);
        }

    }
}
