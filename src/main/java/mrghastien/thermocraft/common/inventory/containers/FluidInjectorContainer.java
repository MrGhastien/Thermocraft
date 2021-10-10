package mrghastien.thermocraft.common.inventory.containers;

import mrghastien.thermocraft.common.registries.ModContainers;
import mrghastien.thermocraft.common.tileentities.FluidInjectorTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

public class FluidInjectorContainer extends BaseContainer<FluidInjectorTile> {


    public FluidInjectorContainer(FluidInjectorTile tileEntity, int id, PlayerInventory playerInventory) {
        super(ModContainers.FLUID_INJECTOR.get(), id, playerInventory, tileEntity, 1);
        addSlot(new SlotItemHandler(tileEntity.getInput(), 0, 62, 35));
        addSlot(new SlotItemHandler(tileEntity.getOutput(), 0, 112, 35));
        layoutPlayerInventorySlots(8, 93);
    }

    public static FluidInjectorContainer createClient(int id, PlayerInventory inv, PacketBuffer data) {
        BlockPos pos = data.readBlockPos();
        World world = inv.player.level;
        TileEntity te = world.getBlockEntity(pos);
        if(te instanceof FluidInjectorTile) {
            return new FluidInjectorContainer((FluidInjectorTile) te, id, inv);
        }
        return null;
    }
}
