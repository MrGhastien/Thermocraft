package mrghastien.thermocraft.common.blocks;

import mrghastien.thermocraft.common.tileentities.ThermalCapacitorTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ThermalCapacitorBlock extends Block {

    public ThermalCapacitorBlock() {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).noOcclusion());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ThermalCapacitorTile();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!world.isClientSide()) {
            TileEntity tileentity = world.getBlockEntity(pos);
            if(tileentity instanceof INamedContainerProvider) {
                NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider) tileentity, tileentity.getBlockPos());
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }
}
