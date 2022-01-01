package mrghastien.thermocraft.common.blocks.transmitters.conductor;

import mrghastien.thermocraft.common.blocks.transmitters.HeatTransmitterBlock;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.blocks.transmitters.conductor.HeatConductorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeatConductorBlock extends HeatTransmitterBlock {

    public HeatConductorBlock() {
        super(Properties.of(Material.METAL).noOcclusion().strength(5f));
    }


    @Override
    public HeatNetworkHandler.HeatNetworkType getNetworkType() {
        return HeatNetworkHandler.HeatNetworkType.CONDUCTOR;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
        return new HeatConductorBlockEntity(blockPos, blockState);
    }
}
