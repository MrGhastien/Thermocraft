package mrghastien.thermocraft.common.blocks.machines.tartanicholder;

import mrghastien.thermocraft.common.registries.ModItems;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TartanicHolderBlock extends Block implements EntityBlock {

    public TartanicHolderBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL).noOcclusion());
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if(world.isClientSide()) return InteractionResult.SUCCESS;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TartanicHolderBlockEntity tartanicHolder) {
            ItemStack heldItem = player.getItemInHand(hand);
            if(heldItem.is(ModItems.TARTANE_CRYSTAL.get())) {
                tartanicHolder.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                    h.insertItem(0, heldItem, false);
                });
            }
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        return new TartanicHolderBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        if(pBlockEntityType == ModTileEntities.TARTANIC_HOLDER.get()) {
            if(pLevel.isClientSide())
                return (pLevel1, pPos, pState1, pBlockEntity) -> ((TartanicHolderBlockEntity)pBlockEntity).clientTick();
            return (pLevel1, pPos, pState1, pBlockEntity) -> ((TartanicHolderBlockEntity)pBlockEntity).serverTick();
        }
        return null;
    }
}
