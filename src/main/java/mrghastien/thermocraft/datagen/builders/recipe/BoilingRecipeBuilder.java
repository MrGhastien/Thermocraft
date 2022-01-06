package mrghastien.thermocraft.datagen.builders.recipe;

import com.google.gson.JsonObject;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.crafting.FluidIngredient;
import mrghastien.thermocraft.common.crafting.StackIngredient;
import mrghastien.thermocraft.common.registries.ModBlocks;
import mrghastien.thermocraft.datagen.CriterionHelper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BoilingRecipeBuilder extends ModRecipeBuilder<BoilingRecipeBuilder> {

	private FluidIngredient inputFluid;
	private StackIngredient inputItem;
	private final FluidStack resultFluid;
	private ItemStack resultItem;
	private final double inputHeatCapacity;
	
	private BoilingRecipeBuilder(Fluid resultFluid, int amount, double inputHeatCapacity) {
		super(new ResourceLocation(ThermoCraft.MODID, "boiling"));
		this.resultFluid = new FluidStack(resultFluid, amount);
		this.inputHeatCapacity = inputHeatCapacity;
	}
	
	public static BoilingRecipeBuilder boiling(Fluid result, int count, double inputHeatCapacity) {
		return new BoilingRecipeBuilder(result, count, inputHeatCapacity).unlockedBy("placed_boiler", CriterionHelper.placedBlock(ModBlocks.BOILER.getBlock()));
	}
	
	public BoilingRecipeBuilder ingredient(FluidIngredient ingredient) {
		this.inputFluid = ingredient;
		return this;
	}
	
	public BoilingRecipeBuilder ingredient(Fluid fluid, int amount) {
		return ingredient(FluidIngredient.of(new FluidStack(fluid, amount)));
	}
	
	public BoilingRecipeBuilder ingredient(Tag.Named<Fluid> tag, int amount) {
		return ingredient(FluidIngredient.of(tag, amount));
	}

	public void save(Consumer<FinishedRecipe> consumer) {
		this.save(consumer, new ResourceLocation(ThermoCraft.MODID, "boiling/" + resultFluid.getFluid().getRegistryName().getPath()));
	}

	@Override
	protected void validate(ResourceLocation id) {
		super.validate(id);
		if(resultFluid.isEmpty() && resultItem.isEmpty())
			throw new IllegalStateException("A boiling recipe must have at least one output");

		if((inputItem == null || inputItem.isEmpty()) && (inputFluid == null || inputFluid.isEmpty()))
			throw new IllegalStateException("A boiling recipe must have at least one input");
	}

	public BoilingRecipeBuilder itemIngredient(StackIngredient ingredient) {
		this.inputItem = ingredient;
		return this;
	}

	public BoilingRecipeBuilder itemIngredient(Item fluid, int count) {
		return itemIngredient(StackIngredient.of(new ItemStack(fluid, count)));
	}

	public BoilingRecipeBuilder itemIngredient(Tag.Named<Item> tag, int count) {
		return itemIngredient(StackIngredient.of(tag, count));
	}

	public BoilingRecipeBuilder resultItem(ItemStack itemStack) {
		this.resultItem = itemStack;
		return this;
	}
	public BoilingRecipeBuilder resultItem(Item item, int count) {
		return resultItem(new ItemStack(item, count));
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
		public void serializeRecipeData(@Nonnull JsonObject json) {

			JsonObject inputs = new JsonObject();
			if(inputFluid != null && !inputFluid.isEmpty()) inputs.add("fluid", inputFluid.toJson());
			if(inputItem != null && !inputItem.isEmpty()) inputs.add("item", inputItem.toJson());
			json.add("inputs", inputs);

			JsonObject results = new JsonObject();
			if(resultFluid != null && !resultFluid.isEmpty()) {
				JsonObject fluidResult = new JsonObject();
				fluidResult.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(resultFluid.getFluid()).toString());
				if (resultFluid.getAmount() > 1) fluidResult.addProperty("amount", resultFluid.getAmount());
				results.add("fluid", fluidResult);
			}

			if(resultItem != null && !resultItem.isEmpty()) {
				JsonObject itemResult = new JsonObject();
				itemResult.addProperty("item", ForgeRegistries.ITEMS.getKey(resultItem.getItem()).toString());
				if (resultItem.getCount() > 1) itemResult.addProperty("count", resultItem.getCount());
				results.add("item", itemResult);
			}
			json.add("results", results);
			json.addProperty("inputHeatCapacity", inputHeatCapacity);
		}
	}
}
