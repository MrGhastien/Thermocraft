package mrghastien.thermocraft.common.blocks;

import mrghastien.thermocraft.common.registries.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.ForgeSoundType;

import javax.annotation.Nullable;

public class BoilerBlock extends BaseMachineBlock {

    public BoilerBlock() {
        super(Properties.of(Material.METAL).sound(ForgeSoundType.METAL));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.BOILER.get().create();
    }
}
