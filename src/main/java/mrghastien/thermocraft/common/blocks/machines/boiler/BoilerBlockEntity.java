package mrghastien.thermocraft.common.blocks.machines.boiler;

import mrghastien.thermocraft.api.capabilities.heat.IHeatHandler;
import mrghastien.thermocraft.api.capabilities.heat.TransferType;
import mrghastien.thermocraft.common.blocks.MachineBlockEntity;
import mrghastien.thermocraft.common.capabilities.Capabilities;
import mrghastien.thermocraft.common.capabilities.fluid.ModFluidHandler;
import mrghastien.thermocraft.common.capabilities.fluid.ModFluidTank;
import mrghastien.thermocraft.common.capabilities.heat.HeatHandler;
import mrghastien.thermocraft.common.capabilities.heat.SidedHeatHandler;
import mrghastien.thermocraft.common.capabilities.item.ModItemStackHandler;
import mrghastien.thermocraft.common.crafting.BoilingRecipe;
import mrghastien.thermocraft.common.crafting.ModRecipeType;
import mrghastien.thermocraft.common.inventory.menus.BoilerMenu;
import mrghastien.thermocraft.common.network.data.IDataHolder;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BoilerBlockEntity extends MachineBlockEntity {

    private final SidedHeatHandler heatHandler = new SidedHeatHandler(1200, 40, 1, this::setChanged, d -> TransferType.INPUT);

    private final ModFluidHandler inputFluidHandler = new ModFluidHandler(new ModFluidTank(5000));
    private final ModFluidHandler outputFluidHandler = new ModFluidHandler(new ModFluidTank(5000));

    private final ModItemStackHandler inputItemHandler = new ModItemStackHandler((i, s) -> ModRecipeType.BOILING.anyMatch(level, r -> r.matches(s)), i -> this.setChanged());
    private final ModItemStackHandler outputItemHandler = new ModItemStackHandler(i -> this.setChanged());

    private final LazyOptional<CombinedInvWrapper> combinedItemHandlers = LazyOptional.of(() -> new CombinedInvWrapper(inputItemHandler, outputItemHandler));

    private boolean running;
    private BoilingRecipe currentRecipe;

    public BoilerBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.BOILER.get(), pos, state);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        heatHandler.getLazy().invalidate();
        inputFluidHandler.getLazy().invalidate();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player player) {
        return new BoilerMenu(id, playerInventory, this);
    }

    @Override
    public void serverTick() {
        //If fluid in input, and the temperature is high enough, consume a certain amount of input per tick
        //depending on the temperature (higher = faster boiling)
        //Also extract energy from the heat handler
        //Finally fill the output tank.

        //this.inputFluidHandler.fill(new FluidStack(Fluids.WATER, 10), IFluidHandler.FluidAction.EXECUTE);
        heatHandler.ambient();
        if(canRun()) {
            this.currentRecipe = getRecipe();

            if (currentRecipe == null) return;
            if(hasRoomInOutput() && hasNecessaryIngredients()) {
                consumeInput();
                heatHandler.transferEnergy(currentRecipe.getInputHeatCapacity() * (IHeatHandler.AIR_TEMPERATURE - heatHandler.getTemperature()));
                storeResult();
            }
        } else {
            currentRecipe = null;
        }
    }

    private void consumeInput() {
        for(FluidStack stack : currentRecipe.getInputFluid().getFluids()) {
            if(inputFluidHandler.contains(stack)) {
                inputFluidHandler.drain(stack.copy(), IFluidHandler.FluidAction.EXECUTE);
                break;
            }
        }

        ItemStack stackInInput = inputItemHandler.getStackInSlot(0);
        for(ItemStack stack : currentRecipe.getInputItem().getItems()) {
            if(stackInInput.sameItem(stack)) {
                inputItemHandler.extractItem(0, stack.getCount(), false);
                break;
            }
        }

    }

    private boolean hasNecessaryIngredients() {
        return currentRecipe.matches(inputItemHandler) && currentRecipe.matches(inputFluidHandler);
    }

    private void storeResult() {
        //outputFluidHandler.fill(currentRecipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
        outputItemHandler.insertItem(0, currentRecipe.getOutputItem().copy(), false);
    }

    private boolean hasRoomInOutput() {
        boolean enoughFluidSpace = outputFluidHandler.fill(currentRecipe.getOutputFluid(), IFluidHandler.FluidAction.SIMULATE) == currentRecipe.getOutputFluid().getAmount();
        boolean enoughItemSpace = outputItemHandler.insertItem(0, currentRecipe.getOutputItem(), true).isEmpty();
        return enoughFluidSpace && enoughItemSpace;
    }

    private BoilingRecipe getRecipe() {
        for(BoilingRecipe recipe : ModRecipeType.BOILING.getRecipes(level).values()) {
            if(recipe.matches(inputFluidHandler) && recipe.matches(inputItemHandler)) return recipe;
        }
        return null;
    }

    private boolean canRun() {
        return heatHandler.getTemperature() > 400 && !inputFluidHandler.isEmpty() || !inputItemHandler.isEmpty();
    }

    public boolean isRunning() {
        return running;
    }

    public ModFluidHandler getInputFluidHandler() {
        return inputFluidHandler;
    }

    public ModFluidHandler getOutputFluidHandler() {
        return outputFluidHandler;
    }

    public HeatHandler getHeatHandler() {
        return heatHandler;
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

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        heatHandler.deserializeNBT(nbt.getCompound("Heat"));
        inputFluidHandler.getTank(0).setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound("InputFluid")));
        inputItemHandler.deserializeNBT(nbt.getCompound("InputItem"));
        outputFluidHandler.getTank(0).setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound("OutputFluid")));
        outputItemHandler.deserializeNBT(nbt.getCompound("OutputItem"));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("Heat", heatHandler.serializeNBT());
        nbt.put("InputFluid", inputFluidHandler.getFluidInTank(0).writeToNBT(new CompoundTag()));
        nbt.put("InputItem", inputItemHandler.serializeNBT());
        nbt.put("Output", outputFluidHandler.getFluidInTank(0).writeToNBT(new CompoundTag()));
        nbt.put("OutputItem", outputItemHandler.serializeNBT());
    }

    @Override
    public void registerSyncData(IDataHolder holder) {
        super.registerSyncData(holder);
        if(holder.getCategory() != IDataHolder.DataHolderCategory.CONTAINER) return;

        heatHandler.gatherData(holder);
        inputFluidHandler.gatherData("input", holder);
        outputFluidHandler.gatherData("output", holder);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.HEAT_HANDLER_CAPABILITY) return (side == null ? heatHandler.getLazy() : heatHandler.getLazy(side)).cast();
        else if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if(side == Direction.WEST || side == null) return inputFluidHandler.getLazy().cast();
            if(side == Direction.UP) return outputFluidHandler.getLazy().cast();
        } else if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == Direction.UP) return inputItemHandler.getLazy().cast();
            if(side == Direction.WEST) return outputItemHandler.getLazy().cast();
            if(side == null) return combinedItemHandlers.cast();
        }
        return super.getCapability(cap, side);
    }
}
