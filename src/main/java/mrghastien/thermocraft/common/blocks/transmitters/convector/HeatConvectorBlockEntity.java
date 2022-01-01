package mrghastien.thermocraft.common.blocks.transmitters.convector;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.blocks.transmitters.HeatTransmitterTile;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.ConvectorCable;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class HeatConvectorBlockEntity extends HeatTransmitterTile<ConvectorCable> {

    protected Fluid fluid;

    public HeatConvectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.HEAT_CONVECTOR.get(), pos, state);
    }

    public void setFluid(Fluid fluid) {
        this.fluid = fluid;
        cable.setFluid(fluid);
        ThermoCraft.LOGGER.debug("Set fluid in TE");
    }

    public Fluid getFluid() {
        return fluid;
    }

    @Override
    protected ConvectorCable createCable() {
        ThermoCraft.LOGGER.debug("Created cable");
        return new ConvectorCable(level, worldPosition, this);
    }
}
