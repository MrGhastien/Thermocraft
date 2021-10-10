package mrghastien.thermocraft.common.crafting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class BaseRecipe implements IRecipe<BaseRecipe.DummyInventory> {

    private final ResourceLocation id;

    public BaseRecipe(ResourceLocation id) {
        this.id = id;
    }

    //VANILLA

    @Deprecated
    @Override
    public boolean matches(DummyInventory inv, World worldIn) {
        return true;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Deprecated
    @Override
    public ItemStack assemble(DummyInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Deprecated
    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class DummyInventory implements IInventory {

        private static final DummyInventory INSTANCE = new DummyInventory();

        public static DummyInventory getInstance() {
            return INSTANCE;
        }

        private DummyInventory() {}

        @Override
        public void clearContent() {}

        @Override
        public int getContainerSize() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ItemStack getItem(int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int index, int count) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int index, ItemStack stack) {}

        @Override
        public void setChanged() {}

        @Override
        public boolean stillValid(PlayerEntity player) {
            return false;
        }
    }

    //Just to shorten the code
    public abstract static class Serializer<T extends BaseRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
    }
}
