package mrghastien.thermocraft.common.inventory.containers;

import mrghastien.thermocraft.common.registries.ModContainers;
import mrghastien.thermocraft.common.tileentities.ThermalCapacitorTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ThermalCapacitorContainer extends BaseContainer {

    public ThermalCapacitorContainer(int id, Inventory playerInventory, ThermalCapacitorTile tileEntity) {
        super(ModContainers.THERMAL_CAPACITOR.get(), id, playerInventory, tileEntity, 0);
        layoutPlayerInventorySlots(8, 93);
    }

    public static ThermalCapacitorContainer createClient(int id, Inventory inv, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.level;
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof ThermalCapacitorTile) {
            return new ThermalCapacitorContainer(id, inv, (ThermalCapacitorTile) te);
        }
        return null;
    }
}
