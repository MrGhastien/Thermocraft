package mrghastien.thermocraft.common.blocks.transmitters.convector;

import mrghastien.thermocraft.common.blocks.transmitters.HeatTransmitterBlock;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeatConvectorPumpBlock extends HeatTransmitterBlock {

    public HeatConvectorPumpBlock() {
        super(Properties.of(Material.METAL).strength(7.5f).noOcclusion());
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (world.isClientSide()) return InteractionResult.SUCCESS;
        BlockEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof MenuProvider) {
            NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) tileentity, tileentity.getBlockPos());
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public HeatNetworkHandler.HeatNetworkType getNetworkType() {
        return HeatNetworkHandler.HeatNetworkType.CONVECTOR;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        //TODO: Make a proper voxel shape
        return Shapes.block();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
        return new HeatConvectorPumpBlockEntity(blockPos, blockState);
    }
}
