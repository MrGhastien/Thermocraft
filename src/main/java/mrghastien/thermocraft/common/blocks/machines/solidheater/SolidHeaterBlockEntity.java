package mrghastien.thermocraft.common.blocks.machines.solidheater;

import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.blocks.MachineBlockEntity;
import mrghastien.thermocraft.common.capabilities.Capabilities;
import mrghastien.thermocraft.common.capabilities.heat.HeatHandler;
import mrghastien.thermocraft.common.capabilities.heat.SidedHeatHandler;
import mrghastien.thermocraft.common.capabilities.item.ModItemStackHandler;
import mrghastien.thermocraft.common.inventory.menus.SolidHeaterContainer;
import mrghastien.thermocraft.common.network.data.DataType;
import mrghastien.thermocraft.common.network.data.IDataHolder;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import mrghastien.thermocraft.util.MathUtils;
import mrghastien.thermocraft.util.math.FixedPointNumber;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SolidHeaterBlockEntity extends MachineBlockEntity {

    private static final int ENERGY_PRODUCTION = 3200;
    private static final double FURNACE_LIT_TEMPERATURE = 360;

    private int burnTime = 0;

    private int totalBurnTime = 0;

    private boolean running = false;

    private final SidedHeatHandler heatHandler = new SidedHeatHandler(1200, 40.0, 1, this::setChanged, d -> TransferType.OUTPUT);

    private final ModItemStackHandler inputInv = new ModItemStackHandler((slot, stack) -> ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0, slot-> this.setChanged());

    public SolidHeaterBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.SOLID_HEATER.get(), pos, state);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new SolidHeaterContainer(this, id, inv);
    }

    @Override
    public void registerSyncData(IDataHolder holder) {
        super.registerSyncData(holder);
        if(holder.getCategory() != IDataHolder.DataHolderCategory.CONTAINER) return;
        heatHandler.gatherData(holder);
        holder.addData(DataType.INT, ThermoCraft.modLoc("burn_time"), this::getBurnTime, this::setBurnTime);
        holder.addData(DataType.INT, ThermoCraft.modLoc("total_burn_time"), this::getTotalBurnTime, this::setTotalBurnTime);
    }

    @Override
    public void serverTick() {
        if (burnTime <= 0 && containsFuel()) {
            consumeFuel();
            setActiveState();
        }

        heatHandler.ambient();
        if (burnTime > 0) {
            burnTime--;
            heatHandler.transferEnergy(null, ENERGY_PRODUCTION);
        } else {
            setInactiveState();
        }

        BlockEntity te = level.getBlockEntity(worldPosition.above());
        if(te == null) return;
        if(te instanceof AbstractFurnaceBlockEntity) {
            AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) te;
            if(furnace.getItem(1) == ItemStack.EMPTY) {
                furnace.litTime = (int) MathUtils.clampedMap(heatHandler.getTemperature(), FURNACE_LIT_TEMPERATURE, 500, 0, 1200);
                furnace.litDuration = 1200;
                if(heatHandler.getTemperature() >= FURNACE_LIT_TEMPERATURE && !level.getBlockState(worldPosition.above()).getValue(AbstractFurnaceBlock.LIT))
                    this.level.setBlock(this.worldPosition.above(), this.level.getBlockState(this.worldPosition.above()).setValue(AbstractFurnaceBlock.LIT, true), 3);
            }
        } else {
            te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, Direction.DOWN).ifPresent(h -> {
                FixedPointNumber energy = FixedPointNumber.valueOf(Math.min(heatHandler.getConductionCoefficient(), h.getConductionCoefficient()) * (heatHandler.getTemperature() - h.getTemperature()));
                if(energy.isGreaterThan(0))  {
                    h.transferEnergy(energy);
                    heatHandler.transferEnergy(Direction.UP, energy.negate());
                }
            });
        }
    }

    protected BlockState getInactiveState(BlockState state) {
        return state.setValue(BlockStateProperties.LIT, false);
    }

    protected BlockState getActiveState(BlockState state) {
        return state.setValue(BlockStateProperties.LIT, true);
    }

    protected void updateBlockState(BlockState newState) {
        if (level == null) return;
        BlockState oldState = level.getBlockState(worldPosition);
        if (oldState != newState) {
            level.setBlock(worldPosition, newState, 3);
            //level.notifyBlockUpdate(worldPosition, oldState, newState, 3);
        }
    }

    protected void setInactiveState() {
        updateBlockState(getInactiveState(level.getBlockState(worldPosition)));
        running = false;
    }

    protected void setActiveState() {
        updateBlockState(getActiveState(level.getBlockState(worldPosition)));
        running = true;
    }

    private boolean containsFuel() {
        for(int i = 0; i < inputInv.getSlots(); i++) {
            if(ForgeHooks.getBurnTime(inputInv.getStackInSlot(i), RecipeType.SMELTING) > 0) {
                return true;
            }
        }
        return false;
    }

    private void consumeFuel() {
        ItemStack stack = inputInv.getStackInSlot(0);
        if(stack != ItemStack.EMPTY) {
            burnTime = totalBurnTime = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
            inputInv.extractItem(0, 1, false);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        heatHandler.getLazy().invalidate();
    }

    @Override
    public void loadInternal(CompoundTag nbt) {
        inputInv.deserializeNBT(nbt.getCompound("Input"));
        heatHandler.deserializeNBT(nbt.getCompound("Heat"));
		/*NBT shape :
			Root:{
				Input :{
					Items:...
					Size:...
					...
				}
				Heat:{
					InternalEnergy:...
					HeatCapacity:...
				}
			}

		*/
    }

    @Override
    public void saveInternal(CompoundTag nbt) {
        nbt.put("Input", inputInv.serializeNBT());
        nbt.put("Heat", heatHandler.serializeNBT());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.HEAT_HANDLER_CAPABILITY) return (side == null ? heatHandler.getLazy() : heatHandler.getLazy(side)).cast();
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inputInv.getLazy().cast();
        }
        return super.getCapability(cap, side);
    }

    //Accessors

    public int getTotalBurnTime() {
        return totalBurnTime;
    }

    public void setTotalBurnTime(int totalBurnTime) {
        this.totalBurnTime = totalBurnTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public HeatHandler getHeatHandler() {
        return heatHandler;
    }

    public ModItemStackHandler getInputInv() {
        return inputInv;
    }
}
