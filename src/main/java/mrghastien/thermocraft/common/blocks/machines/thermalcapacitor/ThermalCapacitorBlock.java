package mrghastien.thermocraft.common.blocks.machines.thermalcapacitor;

import mrghastien.thermocraft.common.blocks.MachineBlock;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ThermalCapacitorBlock extends MachineBlock {

    public ThermalCapacitorBlock() {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).noOcclusion());
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if(!world.isClientSide()) {
            BlockEntity tileentity = world.getBlockEntity(pos);
            if(tileentity instanceof MenuProvider) {
                NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) tileentity, tileentity.getBlockPos());
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
        return new ThermalCapacitorBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState blockState, @Nonnull BlockEntityType<T> type) {
        return createTicker(level, type, ModTileEntities.THERMAL_CAPACITOR.get());
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

    }
}
