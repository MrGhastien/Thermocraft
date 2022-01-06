package mrghastien.thermocraft.common.capabilities.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class ModItemStackHandler extends ItemStackHandler {

    private final LazyOptional<ModItemStackHandler> lazy;
    private final BiPredicate<Integer, ItemStack> validator;
    private final Consumer<Integer> changeListener;

    public ModItemStackHandler(BiPredicate<Integer, ItemStack> validator, Consumer<Integer> changeListener) {
        this(1, validator, changeListener);
    }

    public ModItemStackHandler(Consumer<Integer> changeListener) {
        this(1, (slot, stack) -> true, changeListener);
    }

    public ModItemStackHandler(int size, BiPredicate<Integer, ItemStack> validator, Consumer<Integer> changeListener) {
        super(size);
        this.validator = validator;
        this.changeListener = changeListener;
        this.lazy = LazyOptional.of(() -> this);
    }

    public ModItemStackHandler(NonNullList<ItemStack> stacks, BiPredicate<Integer, ItemStack> validator, Consumer<Integer> changeListener) {
        super(stacks);
        this.validator = validator;
        this.changeListener = changeListener;
        this.lazy = LazyOptional.of(() -> this);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return validator.test(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        changeListener.accept(slot);
    }

    public LazyOptional<ModItemStackHandler> getLazy() {
        return lazy;
    }

    public boolean isEmpty() {
        for(ItemStack stack : stacks) {
            if(!stack.isEmpty()) return false;
        }
        return true;
    }
}
