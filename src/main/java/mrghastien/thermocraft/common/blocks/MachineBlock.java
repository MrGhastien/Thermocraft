package mrghastien.thermocraft.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;

public abstract class MachineBlock extends Block implements EntityBlock {

    public MachineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if(tileentity != null) {
            LazyOptional<IItemHandler> handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            handler.ifPresent(h -> {
                for (int s = 0; s < h.getSlots(); s++) {
                    ItemStack stack = h.getStackInSlot(s);
                    NonNullList<ItemStack> itemsToDrop = NonNullList.withSize(1, stack);
                    Containers.dropContents(worldIn, pos, itemsToDrop);
                }
            });
            super.playerWillDestroy(worldIn, pos, state, player);
        }
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        return Direction.getNearest((float) (entity.getX() - clickedBlock.getX()), 0, (float) (entity.getZ() - clickedBlock.getZ()));
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if(world.isClientSide()) return InteractionResult.SUCCESS;
        BlockEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof MenuProvider) {
            NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) tileentity, tileentity.getBlockPos());
        }
        return InteractionResult.CONSUME;
    }

    protected boolean ticksOnClient() { return false; }

    protected boolean ticksOnServer() { return true; }

    private <A extends BlockEntity, E extends MachineBlockEntity> BlockEntityTicker<E> createTickerInternal(Level level, BlockEntityType<A> providedType, BlockEntityType<E> expectedType) {
        if(expectedType != providedType) return null;
        if(level.isClientSide() && ticksOnClient()) return (l, pos, state1, be) -> be.clientTick();
        if(!level.isClientSide() && ticksOnServer()) return (l, pos, state1, be) -> {
            be.serverTick();
            if(be.tickCount % 20 == 0)
                be.broadcastChanges();
            be.tickCount++;
        };
        return null;
    }

    //Forced to do this because somehow a BlockEntity extending BaseTile doesn't extend BlockEntity
    @SuppressWarnings("unchecked")
    protected <A extends BlockEntity, E extends MachineBlockEntity> BlockEntityTicker<A> createTicker(Level level, BlockEntityType<A> providedType, BlockEntityType<E> expectedType) {
        return (BlockEntityTicker<A>) createTickerInternal(level, providedType, expectedType);
    }
}
