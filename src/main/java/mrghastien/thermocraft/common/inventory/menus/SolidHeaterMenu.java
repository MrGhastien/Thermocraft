package mrghastien.thermocraft.common.inventory.menus;


import mrghastien.thermocraft.common.blocks.machines.solidheater.SolidHeaterBlockEntity;
import mrghastien.thermocraft.common.registries.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SolidHeaterMenu extends BaseMenu {

    public SolidHeaterMenu(SolidHeaterBlockEntity tileEntity, int id, Inventory playerInventory) {
        super(ModMenus.SOLID_HEATER.get(), id, playerInventory, tileEntity, 1);
        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> addSlot(new SlotItemHandler(h, 0, 80, 35)));
        layoutPlayerInventorySlots(8, 84);
    }

    public static SolidHeaterMenu createClient(int id, Inventory inv, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.level;
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof SolidHeaterBlockEntity) {
            return new SolidHeaterMenu((SolidHeaterBlockEntity) te, id, inv);
        }
        return null;
    }
}
