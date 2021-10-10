package mrghastien.thermocraft.common.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mrghastien.thermocraft.common.capabilities.fluid.ModFluidHandler;
import mrghastien.thermocraft.common.registries.ModRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FluidInjectionRecipe extends BaseRecipe {

    private final FluidIngredient fluid;
    private final StackIngredient input;
    private final ItemStack output;

    public FluidInjectionRecipe(ResourceLocation id, FluidIngredient fluid, StackIngredient input, ItemStack output) {
        super(id);
        this.fluid = fluid;
        this.input = input;
        this.output = output;
    }

    public boolean itemMatches(ItemStack stack) { return input.test(stack); }

    public boolean matches(FluidStack fluid) {
        return this.fluid.test(fluid);
    }

    public int getInputCount() {
        return input.getItems()[0].getCount();
    }

    public int getFluidAmount() { return fluid.getFluids()[0].getAmount(); }

    public boolean matches(ModFluidHandler handler) {
        for(int i = 0; i < handler.getTanks(); i++) {
            FluidStack stack = handler.getFluidInTank(i);
            if(matches(stack)) return true;
        }
        return false;
    }

    public FluidIngredient getFluidIngredient() {
        return fluid;
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.FLUID_INJECTION.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRecipeType.FLUID_INJECTION;
    }

    public static class Serializer extends BaseRecipe.Serializer<FluidInjectionRecipe> {

        @Override
        public FluidInjectionRecipe fromJson(ResourceLocation id, JsonObject json) {
            if(!json.has("input")) throw new JsonSyntaxException("Fluid Injection recipe must have an input item");
            if(!json.has("fluid")) throw new JsonSyntaxException("Fluid Injection recipe must have an input fluid");
            if(!json.has("output")) throw new JsonSyntaxException("Fluid Injection recipe must have a result");

            JsonObject inputObject = JSONUtils.getAsJsonObject(json, "input");
            JsonObject fluidObject = JSONUtils.getAsJsonObject(json, "fluid");
            JsonObject outputObject = JSONUtils.getAsJsonObject(json, "output");

            StackIngredient ingredient = (StackIngredient) CraftingHelper.getIngredient(inputObject);
            FluidIngredient fluidStackInput = (FluidIngredient) CraftingHelper.getIngredient(fluidObject);
            ItemStack result = CraftingHelper.getItemStack(outputObject, true);
            return new FluidInjectionRecipe(id, fluidStackInput, ingredient, result);
        }

        @Nullable
        @Override
        public FluidInjectionRecipe fromNetwork(ResourceLocation id, PacketBuffer buf) {
            StackIngredient ingredient = (StackIngredient) CraftingHelper.getIngredient(FluidIngredient.Serializer.ID, buf);
            FluidIngredient fluidIngredient = (FluidIngredient) CraftingHelper.getIngredient(FluidIngredient.Serializer.ID, buf);
            ItemStack result = buf.readItem();

            return new FluidInjectionRecipe(id, fluidIngredient, ingredient, result);
        }

        @Override
        public void toNetwork(PacketBuffer buf, FluidInjectionRecipe recipe) {
            CraftingHelper.write(buf, recipe.input);
            CraftingHelper.write(buf, recipe.fluid);
            buf.writeItem(recipe.output);
        }
    }
}
