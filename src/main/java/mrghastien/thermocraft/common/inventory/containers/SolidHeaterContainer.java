package mrghastien.thermocraft.common.inventory.containers;

import mrghastien.thermocraft.common.registries.ModContainers;
import mrghastien.thermocraft.common.tileentities.SolidHeaterTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SolidHeaterContainer extends BaseContainer {

    public SolidHeaterContainer(SolidHeaterTile tileEntity, int id, PlayerInventory playerInventory) {
        super(ModContainers.SOLID_HEATER.get(), id, playerInventory, tileEntity, 1);
        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> addSlot(new SlotItemHandler(h, 0, 80, 35)));
        layoutPlayerInventorySlots(8, 84);
    }

    public static SolidHeaterContainer createClient(int id, PlayerInventory inv, PacketBuffer data) {
        BlockPos pos = data.readBlockPos();
        World world = inv.player.level;
        TileEntity te = world.getBlockEntity(pos);
        if(te instanceof SolidHeaterTile) {
            return new SolidHeaterContainer((SolidHeaterTile) te, id, inv);
        }
        return null;
    }
}
