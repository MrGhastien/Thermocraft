package mrghastien.thermocraft.common.inventory.menus;

import mrghastien.thermocraft.common.blocks.machines.thermalcapacitor.ThermalCapacitorBlockEntity;
import mrghastien.thermocraft.common.registries.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ThermalCapacitorMenu extends BaseMenu {

    public ThermalCapacitorMenu(int id, Inventory playerInventory, ThermalCapacitorBlockEntity tileEntity) {
        super(ModMenus.THERMAL_CAPACITOR.get(), id, playerInventory, tileEntity, 0);
        layoutPlayerInventorySlots(8, 93);
    }

    public static ThermalCapacitorMenu createClient(int id, Inventory inv, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.level;
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof ThermalCapacitorBlockEntity) {
            return new ThermalCapacitorMenu(id, inv, (ThermalCapacitorBlockEntity) te);
        }
        return null;
    }
}
