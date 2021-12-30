package mrghastien.thermocraft.common.tileentities;

import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.common.capabilities.Capabilities;
import mrghastien.thermocraft.common.capabilities.heat.HeatHandler;
import mrghastien.thermocraft.common.capabilities.heat.SidedHeatHandler;
import mrghastien.thermocraft.common.inventory.containers.ThermalCapacitorContainer;
import mrghastien.thermocraft.common.network.data.IDataHolder;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import mrghastien.thermocraft.util.Constants;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ThermalCapacitorTile extends BaseTile {

    private final SidedHeatHandler heatHandler = new SidedHeatHandler(1400, 100, 0.05, this::setChanged, d -> TransferType.BOTH);

    public ThermalCapacitorTile() {
        super(ModTileEntities.THERMAL_CAPACITOR.get(), true);
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return new ThermalCapacitorContainer(id, inv, this);
    }

    @Override
    public void serverTick() {
        transferHeatToNearbyBlocks();
        heatHandler.ambient();
    }

    private void transferHeatToNearbyBlocks() {
        for(Direction dir : Constants.DIRECTIONS) {
            TileEntity te = level.getBlockEntity(worldPosition.relative(dir));
            if(te == null) continue;
            Direction facing = dir.getOpposite();
            te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, facing).ifPresent(h -> {
                if(!h.canReceive()) return;
                long energy = (long) (Math.min(heatHandler.getConductionCoefficient(), h.getConductionCoefficient()) * (h.getTemperature() - heatHandler.getTemperature()));
                h.transferEnergy(-energy);
                heatHandler.transferEnergy(dir, energy);
            });
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.HEAT_HANDLER_CAPABILITY) return (side == null ? heatHandler.getLazy() : heatHandler.getLazy(side)).cast();
        return super.getCapability(cap, side);
    }

    public HeatHandler getHeatHandler() {
        return heatHandler;
    }

    @Override
    protected void loadInternal(BlockState state, CompoundNBT nbt) {
        heatHandler.deserializeNBT(nbt.getCompound("Heat"));
    }

    @Override
    protected void saveInternal(CompoundNBT nbt) {
        nbt.put("Heat", heatHandler.serializeNBT());
    }

    @Override
    public void registerSyncData(IDataHolder holder) {
        super.registerSyncData(holder);
        if(holder.getCategory() == IDataHolder.DataHolderCategory.TILE_ENTITY)
            heatHandler.gatherData(holder);
    }
}
