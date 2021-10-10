package mrghastien.thermocraft.common.blocks;

import mrghastien.thermocraft.common.tileentities.FluidInjectorTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

public class FluidInjectorBlock extends BaseMachineBlock {

    public FluidInjectorBlock() {
        super(Properties.of(Material.METAL).sound(ForgeSoundType.METAL));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FluidInjectorTile();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!world.isClientSide()) {
            TileEntity te = world.getBlockEntity(pos);
            if(te instanceof FluidInjectorTile) {
                AtomicReference<FluidActionResult> result = new AtomicReference<>();
                te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(h -> 
                        player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(p ->
                                result.set(FluidUtil.tryEmptyContainerAndStow(player.getItemInHand(hand), h, p, 1000, player, true))));
                if(result.get().isSuccess()) return ActionResultType.SUCCESS;
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }
}
