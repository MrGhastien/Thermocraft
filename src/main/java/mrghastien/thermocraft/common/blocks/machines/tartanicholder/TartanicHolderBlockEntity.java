package mrghastien.thermocraft.common.blocks.machines.tartanicholder;

import mrghastien.thermocraft.common.blocks.BaseBlockEntity;
import mrghastien.thermocraft.common.capabilities.Capabilities;
import mrghastien.thermocraft.common.capabilities.heat.HeatHandler;
import mrghastien.thermocraft.common.capabilities.item.ModItemStackHandler;
import mrghastien.thermocraft.common.capabilities.tartanicflux.TartanicFluxHandler;
import mrghastien.thermocraft.common.registries.ModItems;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import mrghastien.thermocraft.util.Constants;
import mrghastien.thermocraft.util.math.FixedPointNumber;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TartanicHolderBlockEntity extends BaseBlockEntity {

    private long tickCount = 0;

    private static final long OUTPUT_RATE = 128;
    private static final long CONVERSION_RATE = 128;

    private final TartanicFluxHandler fluxHandler = new TartanicFluxHandler(55000, 50000, false, true, true);
    private final HeatHandler heatHandler = new HeatHandler(1000, 40, 0.5, this::setChanged);
    private final ModItemStackHandler itemHandler = new ModItemStackHandler((i, s) -> s.is(ModItems.TARTANE_CRYSTAL.get()), i -> this.setChanged()) {

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? 1 : 64;
        }

    };

    public TartanicHolderBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.TARTANIC_HOLDER.get(), pos, state);
    }

    protected void clientTick() {
        tickCount++;
    }

    protected void serverTick() {
        if(heatHandler.getTemperature() > 300.0) {
            heatHandler.transferEnergy(-CONVERSION_RATE);
            fluxHandler.transferFlux(CONVERSION_RATE, false);
        }

        for(Direction dir : Constants.DIRECTIONS) {
            BlockEntity te = level.getBlockEntity(worldPosition.relative(dir));
            if(te == null) continue;
            Direction facing = dir.getOpposite();
            te.getCapability(Capabilities.TARTANIC_FLUX_HANDLER_CAPABILITY, facing).ifPresent(h -> {
                if(!h.canReceive()) return;
                FixedPointNumber result = h.transferFlux(FixedPointNumber.valueOf(OUTPUT_RATE));
                fluxHandler.transferFlux(result.negate());
            });
        }
        tickCount++;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.HEAT_HANDLER_CAPABILITY)
            return heatHandler.getLazy().cast();
        if(cap == Capabilities.TARTANIC_FLUX_HANDLER_CAPABILITY)
            return fluxHandler.lazy().cast();
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return itemHandler.getLazy().cast();
        return super.getCapability(cap, side);
    }
}
