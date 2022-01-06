package mrghastien.thermocraft.common.crafting;

import com.google.gson.JsonObject;
import mrghastien.thermocraft.common.registries.ModRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BoilingRecipe extends BaseRecipe {

    private final FluidIngredient inputFluid;
    private final StackIngredient inputItem;
    private final FluidStack outputFluid;
    private final ItemStack outputItem;
    private final double inputHeatCapacity;

    public BoilingRecipe(ResourceLocation id, FluidIngredient inputFluid, StackIngredient inputItem, FluidStack outputFluid, ItemStack outputItem, double inputHeatCapacity) {
        super(id);
        this.inputFluid = inputFluid;
        this.inputItem = inputItem;
        this.outputFluid = outputFluid;
        this.outputItem = outputItem;
        this.inputHeatCapacity = inputHeatCapacity * 0.05;
    }

    public boolean matches(FluidStack... fluids) {
        for(FluidStack fluid : fluids) {
            if(inputFluid.test(fluid)) return true;
        }
        return false;
    }

    public boolean matches(FluidStack fluid) {
        return inputFluid.test(fluid);
    }

    public boolean matches(IFluidHandler handler) {
        for(int i = 0; i < handler.getTanks(); i++) {
            FluidStack stack = handler.getFluidInTank(i);
            if(matches(stack)) return true;
        }
        return false;
    }
    public boolean matches(ItemStack... fluids) {
        for(ItemStack fluid : fluids) {
            if(inputItem.test(fluid)) return true;
        }
        return false;
    }

    public boolean matches(ItemStack fluid) {
        return inputItem.test(fluid);
    }

    public boolean matches(IItemHandler handler) {
        for(int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if(matches(stack)) return true;
        }
        return false;
    }


    public FluidIngredient getInputFluid() {
        return inputFluid;
    }

    public StackIngredient getInputItem() {
        return inputItem;
    }

    public double getInputHeatCapacity() {
        return inputHeatCapacity;
    }

    public FluidStack getOutputFluid() {
        return outputFluid;
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(inputFluid, inputFluid);
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BOILING.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeType.BOILING;
    }

    public static class Serializer extends BaseRecipe.Serializer<BoilingRecipe> {

        @Nonnull
        @Override
        public BoilingRecipe fromJson(@Nonnull ResourceLocation id, @Nonnull JsonObject json) {
            //Inputs
            JsonObject inputJson = GsonHelper.getAsJsonObject(json, "inputs");
            JsonObject fluidInputJson = GsonHelper.getAsJsonObject(inputJson, "fluid", null);
            JsonObject itemInputJson = GsonHelper.getAsJsonObject(inputJson, "item", null);
            FluidIngredient fluidIngredient = fluidInputJson == null ? FluidIngredient.EMPTY : (FluidIngredient) CraftingHelper.getIngredient(fluidInputJson);
            StackIngredient itemIngredient = itemInputJson == null ? StackIngredient.EMPTY : (StackIngredient) CraftingHelper.getIngredient(itemInputJson);

            //Outputs
            JsonObject resultJson = GsonHelper.getAsJsonObject(json, "results");
            JsonObject fluidResultJson = GsonHelper.getAsJsonObject(resultJson, "fluid", null);
            JsonObject itemResultJson = GsonHelper.getAsJsonObject(resultJson, "item", null);
            FluidStack resultFluid = fluidResultJson == null ? FluidStack.EMPTY : FluidIngredient.getFluidStack(fluidResultJson, false, true);
            ItemStack resultItem = itemResultJson == null ? ItemStack.EMPTY : CraftingHelper.getItemStack(itemResultJson, true, true);

            double heatCapacity = json.get("inputHeatCapacity").getAsDouble();
            return new BoilingRecipe(id, fluidIngredient, itemIngredient, resultFluid, resultItem, heatCapacity);
        }

        @Nullable
        @Override
        public BoilingRecipe fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buf) {
            FluidIngredient inputFluid = (FluidIngredient) CraftingHelper.getIngredient(FluidIngredient.Serializer.ID, buf);
            StackIngredient inputItem = (StackIngredient) CraftingHelper.getIngredient(StackIngredient.Serializer.ID, buf);
            FluidStack outputFluid = buf.readFluidStack();
            ItemStack outputItem = buf.readItem();
            return new BoilingRecipe(id, inputFluid, inputItem, outputFluid, outputItem, buf.readDouble());
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buf, BoilingRecipe recipe) {
            CraftingHelper.write(buf, recipe.inputFluid);
            CraftingHelper.write(buf, recipe.inputItem);
            buf.writeFluidStack(recipe.outputFluid);
            buf.writeItem(recipe.outputItem);
            buf.writeDouble(recipe.inputHeatCapacity);
        }
    }
}
