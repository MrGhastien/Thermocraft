package mrghastien.thermocraft.common.inventory.containers;

import mrghastien.thermocraft.common.registries.ModContainers;
import mrghastien.thermocraft.common.tileentities.cables.HeatConvectorPumpTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConvectorControllerContainer extends BaseContainer<HeatConvectorPumpTile> {

    public ConvectorControllerContainer(int id, PlayerInventory playerInventory, HeatConvectorPumpTile tileEntity) {
        super(ModContainers.HEAT_CONVECTOR_PUMP.get(), id, playerInventory, tileEntity, 0);
        layoutPlayerInventorySlots(8, 93);
    }

    public static ConvectorControllerContainer createClient(int id, PlayerInventory inv, PacketBuffer data) {
        BlockPos pos = data.readBlockPos();
        World world = inv.player.level;
        TileEntity te = world.getBlockEntity(pos);
        if(te instanceof HeatConvectorPumpTile) {
            return new ConvectorControllerContainer(id, inv, (HeatConvectorPumpTile) te);
        }
        throw new NullPointerException("Tile entity cannot be null !");
    }
}
