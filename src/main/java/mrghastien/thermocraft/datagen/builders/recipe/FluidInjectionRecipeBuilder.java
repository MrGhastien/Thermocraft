package mrghastien.thermocraft.datagen.builders.recipe;

import com.google.gson.JsonObject;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.crafting.FluidIngredient;
import mrghastien.thermocraft.common.crafting.StackIngredient;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class FluidInjectionRecipeBuilder extends ModRecipeBuilder<FluidInjectionRecipeBuilder> {

    private final FluidIngredient fluid;
    private final StackIngredient input;
    private final ItemStack output;

    private FluidInjectionRecipeBuilder(FluidIngredient fluid, StackIngredient input, ItemStack output) {
        super(new ResourceLocation(ThermoCraft.MODID, "fluid_injection"));
        this.fluid = fluid;
        this.input = input;
        this.output = output;
    }

    public static FluidInjectionRecipeBuilder recipe(FluidStack fluid, ItemStack input, ItemStack output) {
        return new FluidInjectionRecipeBuilder(FluidIngredient.of(fluid), StackIngredient.of(input), output);
    }

    public static FluidInjectionRecipeBuilder recipe(ITag<Fluid> fluid, int count, ItemStack input, ItemStack output) {
        return new FluidInjectionRecipeBuilder(FluidIngredient.of(fluid, count), StackIngredient.of(input), output);
    }

    public static FluidInjectionRecipeBuilder recipe(ITag<Fluid> fluid, int amount, ITag<Item> input, int count, ItemStack output) {
        return new FluidInjectionRecipeBuilder(FluidIngredient.of(fluid, amount), StackIngredient.of(input, count), output);
    }

    public static FluidInjectionRecipeBuilder recipe(FluidStack fluid, ITag<Item> input, int count, ItemStack output) {
        return new FluidInjectionRecipeBuilder(FluidIngredient.of(fluid), StackIngredient.of(input, count), output);
    }


    public void save(Consumer<IFinishedRecipe> consumer) {
        this.save(consumer, new ResourceLocation(ThermoCraft.MODID, "fluid_injection/" + output.getItem().getRegistryName().getPath()));
    }

    @Override
    protected Result getResult(ResourceLocation id) {
        return new FluidInjectionRecipeResult(id);
    }

    public class FluidInjectionRecipeResult extends Result {

        public FluidInjectionRecipeResult(ResourceLocation id) {
            super(id);
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("input", input.toJson());
            json.add("fluid", fluid.toJson());
            JsonObject outputObject = new JsonObject();

            outputObject.addProperty("item", output.getItem().getRegistryName().toString());
            if(output.getCount() > 1)
                outputObject.addProperty("count", output.getCount());
            if(output.hasTag())
                outputObject.addProperty("nbt", output.getTag().toString());

            json.add("output", outputObject);
        }
    }
}
