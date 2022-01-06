package mrghastien.thermocraft.common.inventory.menus;

import mrghastien.thermocraft.common.blocks.machines.boiler.BoilerBlockEntity;
import mrghastien.thermocraft.common.registries.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BoilerMenu extends BaseMenu {

    public BoilerMenu(int id, Inventory playerInventory, BoilerBlockEntity tileEntity) {
        super(ModMenus.BOILER.get(), id, playerInventory, tileEntity, 0);
        layoutPlayerInventorySlots(8, 93);
        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new SlotItemHandler(h, 0, 17, 51));
            addSlot(new SlotItemHandler(h, 1, 143, 51));
        });
    }

    public static BoilerMenu createClient(int id, Inventory inv, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.level;
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof BoilerBlockEntity) {
            return new BoilerMenu(id, inv, (BoilerBlockEntity) te);
        }
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}