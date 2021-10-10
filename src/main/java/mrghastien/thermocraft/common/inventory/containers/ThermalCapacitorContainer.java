package mrghastien.thermocraft.common.inventory.containers;

import mrghastien.thermocraft.common.registries.ModContainers;
import mrghastien.thermocraft.common.tileentities.ThermalCapacitorTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThermalCapacitorContainer extends BaseContainer<ThermalCapacitorTile> {

    public ThermalCapacitorContainer(int id, PlayerInventory playerInventory, ThermalCapacitorTile tileEntity) {
        super(ModContainers.THERMAL_CAPACITOR.get(), id, playerInventory, tileEntity, 0);
        layoutPlayerInventorySlots(8, 93);
    }

    public static ThermalCapacitorContainer createClient(int id, PlayerInventory inv, PacketBuffer data) {
        BlockPos pos = data.readBlockPos();
        World world = inv.player.level;
        TileEntity te = world.getBlockEntity(pos);
        if(te instanceof ThermalCapacitorTile) {
            return new ThermalCapacitorContainer(id, inv, (ThermalCapacitorTile) te);
        }
        return null;
    }
}
