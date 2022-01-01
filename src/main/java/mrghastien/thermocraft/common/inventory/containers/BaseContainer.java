package mrghastien.thermocraft.common.inventory.containers;

import mrghastien.thermocraft.common.network.INetworkBinding;
import mrghastien.thermocraft.common.network.data.ContainerDataHolder;
import mrghastien.thermocraft.common.network.packets.PacketHandler;
import mrghastien.thermocraft.common.network.packets.UpdateClientContainerPacket;
import mrghastien.thermocraft.common.tileentities.BaseTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public abstract class BaseContainer extends AbstractContainerMenu {

    public final BlockEntity tileEntity;
    protected final IItemHandler playerInventory;
    protected final BlockPos pos;
    protected final Level world;
    protected final int size;

    protected final ContainerDataHolder dataHolder;

    protected BaseContainer(@Nullable MenuType<?> containerType, int id, Inventory playerInventory, BlockEntity tileEntity, int size) {
        super(containerType, id);
        this.tileEntity = tileEntity;
        this.world = tileEntity.getLevel();
        this.pos = tileEntity.getBlockPos();
        this.size = size;
        this.playerInventory = new InvWrapper(playerInventory);
        this.dataHolder = new ContainerDataHolder(this);
        if(tileEntity instanceof BaseTile)
            ((BaseTile) tileEntity).registerSyncData(dataHolder);
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    protected void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9,  18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < this.size) {
                if (!this.moveItemStackTo(itemstack1, this.size, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.size, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        INetworkBinding binding = dataHolder.getBinding();
        if(binding.hasChanged()) {
            for (ContainerListener listener : containerListeners) {
                if(listener instanceof ServerPlayer)
                PacketHandler.sendToPlayer(new UpdateClientContainerPacket(binding), (ServerPlayer) listener);
            }
        }
    }

    public ContainerDataHolder getDataHolder() {
        return dataHolder;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.blockPosition().distSqr(tileEntity.getBlockPos()) < 64;
    }
}
