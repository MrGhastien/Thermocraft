package mrghastien.thermocraft.common.blocks;

import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.tileentities.cables.HeatConductorTile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class HeatConductorBlock extends HeatTransmitterBlock {

    public HeatConductorBlock() {
        super(AbstractBlock.Properties.of(Material.METAL).noOcclusion().strength(5f));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HeatConductorTile();
    }

    @Override
    public HeatNetworkHandler.HeatNetworkType getNetworkType() {
        return HeatNetworkHandler.HeatNetworkType.CONDUCTOR;
    }
}
