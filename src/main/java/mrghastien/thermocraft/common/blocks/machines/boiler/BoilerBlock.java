package mrghastien.thermocraft.common.blocks.machines.boiler;

import mrghastien.thermocraft.common.blocks.MachineBlock;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

public class BoilerBlock extends MachineBlock {

    public BoilerBlock() {
        super(Properties.of(Material.METAL).sound(ForgeSoundType.METAL).strength(4f).noOcclusion());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
        return new BoilerBlockEntity(blockPos, blockState);
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if(!world.isClientSide()) {
            BlockEntity te = world.getBlockEntity(pos);
            if(te instanceof BoilerBlockEntity) {
                AtomicReference<FluidActionResult> result = new AtomicReference<>();
                te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(h ->
                        player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(p ->
                                result.set(FluidUtil.tryEmptyContainerAndStow(player.getItemInHand(hand), h, p, 1000, player, true))));
                if(result.get().isSuccess()) return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public <A extends BlockEntity> BlockEntityTicker<A> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<A> type) {
        return createTicker(level, type, ModTileEntities.BOILER.get());
    }
}
