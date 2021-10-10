package mrghastien.thermocraft.common.inventory.containers;

import mrghastien.thermocraft.common.network.NetworkDataType;
import mrghastien.thermocraft.common.network.NetworkHandler;
import mrghastien.thermocraft.common.network.packets.PacketHandler;
import mrghastien.thermocraft.common.tileentities.BaseTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class BaseContainer<T extends TileEntity & IThermocraftContainerProvider> extends Container {

    public final T tileEntity;
    protected final IItemHandler playerInventory;
    protected final BlockPos pos;
    protected final World world;
    protected final int size;

    protected BaseContainer(@Nullable ContainerType<?> containerType, int id, PlayerInventory playerInventory, T tileEntity, int size) {
        super(containerType, id);
        this.tileEntity = tileEntity;
        this.world = tileEntity.getLevel();
        this.pos = tileEntity.getBlockPos();
        this.size = size;
        this.playerInventory = new InvWrapper(playerInventory);
        assert world != null;
        tileEntity.registerContainerUpdatedData(this);
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
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
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
    public boolean stillValid(PlayerEntity player) {
        return player.blockPosition().distSqr(tileEntity.getBlockPos()) < 64;
    }

    public void registerData(NetworkDataType type, Supplier<Object> getter, Consumer<Object> setter) {
        NetworkHandler.getInstance(world).add(type, PacketHandler.CONTAINER_LISTENERS.with(() -> this), this, getter, setter);
    }

    @Override
    public void removed(PlayerEntity player) {
        NetworkHandler.getInstance(world).remove(this);
        super.removed(player);
    }
}
