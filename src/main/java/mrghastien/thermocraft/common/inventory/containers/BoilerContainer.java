package mrghastien.thermocraft.common.inventory.containers;

import mrghastien.thermocraft.common.registries.ModContainers;
import mrghastien.thermocraft.common.tileentities.BoilerTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BoilerContainer extends BaseContainer {

    public BoilerContainer(int id, PlayerInventory playerInventory, BoilerTile tileEntity) {
        super(ModContainers.BOILER.get(), id, playerInventory, tileEntity, 0);
        layoutPlayerInventorySlots(8, 93);
    }

    public static BoilerContainer createClient(int id, PlayerInventory inv, PacketBuffer data) {
        BlockPos pos = data.readBlockPos();
        World world = inv.player.level;
        TileEntity te = world.getBlockEntity(pos);
        if(te instanceof BoilerTile) {
            return new BoilerContainer(id, inv, (BoilerTile) te);
        }
        return null;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }
}