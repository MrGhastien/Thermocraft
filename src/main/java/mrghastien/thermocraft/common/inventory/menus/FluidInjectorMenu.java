package mrghastien.thermocraft.common.inventory.menus;

import mrghastien.thermocraft.common.blocks.machines.fluidinjector.FluidInjectorBlockEntity;
import mrghastien.thermocraft.common.registries.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class FluidInjectorMenu extends BaseMenu {


    public FluidInjectorMenu(FluidInjectorBlockEntity tileEntity, int id, Inventory playerInventory) {
        super(ModMenus.FLUID_INJECTOR.get(), id, playerInventory, tileEntity, 1);
        addSlot(new SlotItemHandler(tileEntity.getInput(), 0, 62, 35));
        addSlot(new SlotItemHandler(tileEntity.getOutput(), 0, 112, 35));
        layoutPlayerInventorySlots(8, 93);
    }

    public static FluidInjectorMenu createClient(int id, Inventory inv, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.level;
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof FluidInjectorBlockEntity) {
            return new FluidInjectorMenu((FluidInjectorBlockEntity) te, id, inv);
        }
        return null;
    }
}
