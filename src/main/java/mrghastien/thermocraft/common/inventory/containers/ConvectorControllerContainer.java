package mrghastien.thermocraft.common.inventory.containers;

import mrghastien.thermocraft.common.registries.ModContainers;
import mrghastien.thermocraft.common.tileentities.cables.HeatConvectorPumpTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ConvectorControllerContainer extends BaseContainer {

    public ConvectorControllerContainer(int id, Inventory playerInventory, HeatConvectorPumpTile tileEntity) {
        super(ModContainers.HEAT_CONVECTOR_PUMP.get(), id, playerInventory, tileEntity, 0);
        layoutPlayerInventorySlots(8, 93);
    }

    public static ConvectorControllerContainer createClient(int id, Inventory inv, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.level;
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof HeatConvectorPumpTile) {
            return new ConvectorControllerContainer(id, inv, (HeatConvectorPumpTile) te);
        }
        throw new NullPointerException("Tile entity cannot be null !");
    }
}
