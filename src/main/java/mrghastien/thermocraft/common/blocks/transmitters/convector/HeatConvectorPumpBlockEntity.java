package mrghastien.thermocraft.common.blocks.transmitters.convector;

import mrghastien.thermocraft.common.blocks.transmitters.HeatTransmitterTile;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Pump;
import mrghastien.thermocraft.common.inventory.menus.ConvectorControllerContainer;
import mrghastien.thermocraft.common.registries.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeatConvectorPumpBlockEntity extends HeatTransmitterTile<Pump> implements MenuProvider {

    public HeatConvectorPumpBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.HEAT_CONVECTOR_PUMP.get(), pos, state);
    }

    @Override
    protected Pump createCable() {
        return new Pump(level, worldPosition, this);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
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
    public Component getDisplayName() {
        return new TextComponent("NAME");
    }
}