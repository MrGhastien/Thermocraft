package mrghastien.thermocraft.common.tileentities;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.capabilities.fluid.ModFluidTank;
import mrghastien.thermocraft.common.capabilities.fluid.SingletonFluidHandler;
import mrghastien.thermocraft.common.capabilities.item.ModItemStackHandler;
import mrghastien.thermocraft.common.crafting.FluidInjectionRecipe;
import mrghastien.thermocraft.common.crafting.ModRecipeType;
import mrghastien.thermocraft.common.inventory.containers.FluidInjectorContainer;
import mrghastien.thermocraft.common.network.data.DataType;
import mrghastien.thermocraft.common.network.data.IDataHolder;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//TODO: Rearrange hierarchy
public class FluidInjectorTile extends BaseTile {

    private final ModFluidTank tank = new ModFluidTank(5000, f -> ModRecipeType.FLUID_INJECTION.stream(level).anyMatch(r -> r.matches(f)), this::setChanged);
    private final ModItemStackHandler input = new ModItemStackHandler((i, s) -> ModRecipeType.FLUID_INJECTION.stream(level).anyMatch(r -> r.itemMatches(s)), i -> this.setChanged());
    private final ModItemStackHandler output = new ModItemStackHandler(i -> this.setChanged());

    private final LazyOptional<SingletonFluidHandler> tankLazy = LazyOptional.of(() -> new SingletonFluidHandler(tank));

    private int progress;
    private int maxProgress;
    private boolean running;
    private FluidInjectionRecipe currentRecipe;

    private boolean changed;

    public FluidInjectorTile(BlockPos pos, BlockState state) {
        super(ModTileEntities.FLUID_INJECTOR.get(), pos, state);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.changed = true;
    }

    @Override
    public void serverTick() {
        if(canRun()) {
            if(changed) {
                searchRecipe();
                changed = false;
            }
            if(currentRecipe != null) {
                if (progress < maxProgress) {
                    setActiveState();
                    progress++;
                } else {
                    consumeInput();
                    storeResult();
                    progress = 0;
                }
            } else setInactiveState();
        } else setInactiveState();
    }

    private void consumeInput() {
        if(currentRecipe.matches(tank.getFluid()) && currentRecipe.itemMatches(input.getStackInSlot(0))) {
            tank.drain(currentRecipe.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
            input.extractItem(0, currentRecipe.getInputCount(), false);
        }
    }

    private void storeResult() {
        output.insertItem(0, currentRecipe.getOutput().copy(), false);
    }

    private boolean hasSpaceInOutput() {
        return output.getStackInSlot(0).getCount() < output.getSlotLimit(0);
    }

    private FluidInjectionRecipe getRecipe() {
        for(FluidInjectionRecipe recipe : ModRecipeType.FLUID_INJECTION.getRecipes(level).values()) {
            if(recipe.matches(tank.getFluid()) && recipe.itemMatches(input.getStackInSlot(0))) return recipe;
        }
        return null;
    }

    private boolean canRun() {
        return !tank.isEmpty() && !input.getStackInSlot(0).isEmpty() && hasSpaceInOutput();
    }

    private void searchRecipe() {
        currentRecipe = getRecipe();
        maxProgress = currentRecipe == null ? 0 : currentRecipe.getFluidAmount() / 2;
    }

    @Override
    protected void loadInternal(CompoundTag nbt) {
        tank.deserializeNBT(nbt.getCompound("inputTank"));
    }

    @Override
    protected void saveInternal(CompoundTag nbt) {
        nbt.put("inputTank", tank.serializeNBT());
    }

    protected BlockState getInactiveState(BlockState state) {
        return state.setValue(BlockStateProperties.LIT, false);
    }

    protected BlockState getActiveState(BlockState state) {
        return state.setValue(BlockStateProperties.LIT, true);
    }

    protected void setInactiveState() {
        updateBlockState(getInactiveState(level.getBlockState(worldPosition)));
        running = false;
    }

    protected void setActiveState() {
        updateBlockState(getActiveState(level.getBlockState(worldPosition)));
        running = true;
    }

    public boolean isRunning() {
        return running;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new FluidInjectorContainer(this, id, inventory);
    }

    @Override
    public void registerSyncData(IDataHolder holder) {
        super.registerSyncData(holder);
        if(holder.getCategory() != IDataHolder.DataHolderCategory.CONTAINER) return;

        holder.addData(DataType.INT, ThermoCraft.modLoc("progress"), this::getProgress, v -> progress = v);
        holder.addData(DataType.INT, ThermoCraft.modLoc("max_progress"), this::getMaxProgress, v -> maxProgress = v);
        holder.addData(DataType.BOOL, ThermoCraft.modLoc("running"), this::isRunning, v -> running = v);
        tank.gatherData(holder);
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public ModFluidTank getTank() {
        return tank;
    }

    public ModItemStackHandler getInput() {
        return input;
    }

    public ModItemStackHandler getOutput() {
        return output;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return tankLazy.cast();
        } else if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null) {
            switch (side) {
                case NORTH:
                case SOUTH:
                case EAST:
                case WEST:
                    return input.getLazy().cast();

                case DOWN:
                    return output.getLazy().cast();
            }
        }
        return super.getCapability(cap, side);
    }
}
