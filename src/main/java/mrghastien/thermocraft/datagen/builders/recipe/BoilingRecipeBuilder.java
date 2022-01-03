package mrghastien.thermocraft.datagen.builders.recipe;

import com.google.gson.JsonObject;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.crafting.FluidIngredient;
import mrghastien.thermocraft.common.registries.ModBlocks;
import mrghastien.thermocraft.datagen.CriterionHelper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class BoilingRecipeBuilder extends ModRecipeBuilder<BoilingRecipeBuilder> {

	private FluidIngredient input;
	private final FluidStack result;
	private final double inputHeatCapacity;
	
	private BoilingRecipeBuilder(Fluid result, int amount, double inputHeatCapacity) {
		super(new ResourceLocation(ThermoCraft.MODID, "boiling"));
		this.result = new FluidStack(result, amount);
		this.inputHeatCapacity = inputHeatCapacity;
	}
	
	public static BoilingRecipeBuilder boiling(Fluid result, int count, double inputHeatCapacity) {
		return new BoilingRecipeBuilder(result, count, inputHeatCapacity).unlockedBy("placed_boiler", CriterionHelper.placedBlock(ModBlocks.BOILER.getBlock()));
	}
	
	public BoilingRecipeBuilder ingredient(FluidIngredient ingredient) {
		this.input = ingredient;
		return this;
	}
	
	public BoilingRecipeBuilder ingredient(Fluid fluid, int amount) {
		return ingredient(FluidIngredient.of(new FluidStack(fluid, amount)));
	}
	
	public BoilingRecipeBuilder ingredient(Tag.Named<Fluid> tag, int amount) {
		return ingredient(FluidIngredient.of(tag, amount));
	}

	public void save(Consumer<FinishedRecipe> consumer) {
		this.save(consumer, new ResourceLocation(ThermoCraft.MODID, "boiling/" + result.getFluid().getRegistryName().getPath()));
	}

	@Override
	protected Result getResult(ResourceLocation id) {
		return new BoilingRecipeResult(id);
	}

	public class BoilingRecipeResult extends Result {

		public BoilingRecipeResult(ResourceLocation id) {
			super(id);
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("input", input.toJson());

			JsonObject jsonobjectR = new JsonObject();
			jsonobjectR.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(result.getFluid()).toString());
			jsonobjectR.addProperty("amount", result.getAmount());
			json.add("result", jsonobjectR);
			json.addProperty("inputHeatCapacity", inputHeatCapacity);
		}
	}
}
