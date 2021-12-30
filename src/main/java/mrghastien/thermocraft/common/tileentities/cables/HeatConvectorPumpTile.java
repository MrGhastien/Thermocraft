package mrghastien.thermocraft.common.tileentities.cables;

import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Pump;
import mrghastien.thermocraft.common.inventory.containers.ConvectorControllerContainer;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeatConvectorPumpTile extends HeatTransmitterTile<Pump> implements INamedContainerProvider {

    public HeatConvectorPumpTile() {
        super(ModTileEntities.HEAT_CONVECTOR_PUMP.get());
    }

    @Override
    protected Pump createCable() {
        return new Pump(level, worldPosition, this);
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return new ConvectorControllerContainer(id, inv, this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction facing) {
        if(hasNetwork()) {
            Direction currentDir = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                if (facing == currentDir.getOpposite()) {
                    return getNetwork().getLazy().cast();
                }
            }
        }
        return LazyOptional.empty();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("NAME");
    }
}