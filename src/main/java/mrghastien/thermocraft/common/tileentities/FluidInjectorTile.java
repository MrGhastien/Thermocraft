package mrghastien.thermocraft.common.tileentities;

import mrghastien.thermocraft.common.capabilities.fluid.ModFluidTank;
import mrghastien.thermocraft.common.capabilities.fluid.SingletonFluidHandler;
import mrghastien.thermocraft.common.capabilities.item.ModItemStackHandler;
import mrghastien.thermocraft.common.crafting.FluidInjectionRecipe;
import mrghastien.thermocraft.common.crafting.ModRecipeType;
import mrghastien.thermocraft.common.inventory.containers.BaseContainer;
import mrghastien.thermocraft.common.inventory.containers.FluidInjectorContainer;
import mrghastien.thermocraft.common.network.NetworkDataType;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
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

    public FluidInjectorTile() {
        super(ModTileEntities.FLUID_INJECTOR.get());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.changed = true;
    }

    @Override
    public void tick() {
        if(!level.isClientSide()) {
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
        super.tick();
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
    protected void loadInternal(BlockState state, CompoundNBT nbt) {
        tank.deserializeNBT(nbt.getCompound("inputTank"));
    }

    @Override
    protected void saveInternal(CompoundNBT nbt) {
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
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new FluidInjectorContainer(this, id, inventory);
    }

    @Override
    public void registerContainerUpdatedData(BaseContainer c) {
        c.registerData(NetworkDataType.INT, this::getProgress, v -> progress = (int) v);
        c.registerData(NetworkDataType.INT, this::getMaxProgress, v -> maxProgress = (int) v);
        c.registerData(NetworkDataType.BOOLEAN, this::isRunning, v -> running = (boolean) v);
        tank.gatherContainerData(c);
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
