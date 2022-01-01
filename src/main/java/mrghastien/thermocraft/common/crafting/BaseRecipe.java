package mrghastien.thermocraft.common.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class BaseRecipe implements Recipe<BaseRecipe.DummyContainer> {

    private final ResourceLocation id;

    public BaseRecipe(ResourceLocation id) {
        this.id = id;
    }

    //VANILLA

    @Deprecated
    @Override
    public boolean matches(DummyContainer inv, Level worldIn) {
        return true;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Deprecated
    @Override
    public ItemStack assemble(DummyContainer inv) {
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

    public static class DummyContainer implements Container {

        private static final DummyContainer INSTANCE = new DummyContainer();

        public static DummyContainer getInstance() {
            return INSTANCE;
        }

        private DummyContainer() {}

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
        public boolean stillValid(Player player) {
            return false;
        }
    }

    //Just to shorten the code
    public abstract static class Serializer<T extends BaseRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
    }
}
