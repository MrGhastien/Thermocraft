package mrghastien.thermocraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public abstract class BaseMachineBlock extends Block {

    public BaseMachineBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).setValue(BlockStateProperties.LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.LIT);
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        if(tileentity != null) {
            LazyOptional<IItemHandler> handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            handler.ifPresent(h -> {
                for (int s = 0; s < h.getSlots(); s++) {
                    ItemStack stack = h.getStackInSlot(s);
                    NonNullList<ItemStack> itemsToDrop = NonNullList.withSize(1, stack);
                    InventoryHelper.dropContents(worldIn, pos, itemsToDrop);
                }
            });
            super.playerWillDestroy(worldIn, pos, state, player);
        }
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(placer != null) {
            worldIn.setBlock(pos, state.setValue(BlockStateProperties.HORIZONTAL_FACING, getFacingFromEntity(pos, placer)), 3);
        }
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        return Direction.getNearest((float) (entity.getX() - clickedBlock.getX()), 0, (float) (entity.getZ() - clickedBlock.getZ()));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(world.isClientSide()) return ActionResultType.SUCCESS;
        TileEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof INamedContainerProvider) {
            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileentity, tileentity.getBlockPos());
        }
        return ActionResultType.CONSUME;
    }
}
