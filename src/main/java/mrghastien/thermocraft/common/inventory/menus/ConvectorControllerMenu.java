package mrghastien.thermocraft.common.inventory.menus;

import mrghastien.thermocraft.common.registries.ModMenus;
import mrghastien.thermocraft.common.blocks.transmitters.convector.HeatConvectorPumpBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ConvectorControllerMenu extends BaseMenu {

    public ConvectorControllerMenu(int id, Inventory playerInventory, HeatConvectorPumpBlockEntity tileEntity) {
        super(ModMenus.HEAT_CONVECTOR_PUMP.get(), id, playerInventory, tileEntity, 0);
        layoutPlayerInventorySlots(8, 93);
    }

    public static ConvectorControllerMenu createClient(int id, Inventory inv, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.level;
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof HeatConvectorPumpBlockEntity) {
            return new ConvectorControllerMenu(id, inv, (HeatConvectorPumpBlockEntity) te);
        }
        throw new NullPointerException("Tile entity cannot be null !");
    }
}
