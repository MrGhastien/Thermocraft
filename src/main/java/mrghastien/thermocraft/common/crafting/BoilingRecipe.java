package mrghastien.thermocraft.common.crafting;

import com.google.gson.JsonObject;
import mrghastien.thermocraft.common.capabilities.fluid.ModFluidHandler;
import mrghastien.thermocraft.common.registries.ModRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class BoilingRecipe extends BaseRecipe {

    private final FluidIngredient input;
    private final FluidStack output;
    private final double inputHeatCapacity;

    public BoilingRecipe(ResourceLocation id, FluidIngredient input, FluidStack output, double inputHeatCapacity) {
        super(id);
        this.input = input;
        this.output = output;
        this.inputHeatCapacity = inputHeatCapacity * 0.05;
    }

    public boolean matches(FluidStack... fluids) {
        for(FluidStack fluid : fluids) {
            if(input.test(fluid)) return true;
        }
        return false;
    }

    public boolean matches(FluidStack fluid) {
        return input.test(fluid);
    }

    public boolean matches(ModFluidHandler handler) {
        for(int i = 0; i < handler.getTanks(); i++) {
            FluidStack stack = handler.getFluidInTank(i);
            if(matches(stack)) return true;
        }
        return false;
    }

    public FluidIngredient getInput() {
        return input;
    }

    public double getInputHeatCapacity() {
        return inputHeatCapacity;
    }

    public FluidStack getOutput() {
        return output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(input, input);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BOILING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeType.BOILING;
    }

    public static class Serializer extends BaseRecipe.Serializer<BoilingRecipe> {

        @Override
        public BoilingRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonObject jsonelement = GsonHelper.getAsJsonObject(json, "input");
            FluidIngredient ingredient = (FluidIngredient) CraftingHelper.getIngredient(jsonelement);
            JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
            FluidStack result = new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(resultJson.get("fluid").getAsString())), resultJson.get("amount").getAsInt());
            double heatCapacity = json.get("inputHeatCapacity").getAsDouble();
            return new BoilingRecipe(id, ingredient, result, heatCapacity);
        }

        @Nullable
        @Override
        public BoilingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            FluidIngredient input = (FluidIngredient) CraftingHelper.getIngredient(FluidIngredient.Serializer.ID, buf);
            FluidStack output = buf.readFluidStack();
            return new BoilingRecipe(id, input, output, buf.readDouble());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BoilingRecipe recipe) {
            CraftingHelper.write(buf, recipe.input);
            buf.writeFluidStack(recipe.output);
            buf.writeDouble(recipe.inputHeatCapacity);
        }
    }
}
