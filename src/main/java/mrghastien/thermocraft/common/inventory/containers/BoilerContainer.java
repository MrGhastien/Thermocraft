package mrghastien.thermocraft.common.inventory.containers;

import mrghastien.thermocraft.common.registries.ModContainers;
import mrghastien.thermocraft.common.tileentities.BoilerTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BoilerContainer extends BaseContainer {

    public BoilerContainer(int id, Inventory playerInventory, BoilerTile tileEntity) {
        super(ModContainers.BOILER.get(), id, playerInventory, tileEntity, 0);
        layoutPlayerInventorySlots(8, 93);
    }

    public static BoilerContainer createClient(int id, Inventory inv, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.level;
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof BoilerTile) {
            return new BoilerContainer(id, inv, (BoilerTile) te);
        }
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}