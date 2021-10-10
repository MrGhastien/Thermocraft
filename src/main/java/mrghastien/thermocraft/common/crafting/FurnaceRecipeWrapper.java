package mrghastien.thermocraft.common.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class FurnaceRecipeWrapper extends BaseRecipe {

	private final FurnaceRecipe source;
	
	public FurnaceRecipeWrapper(ResourceLocation id, FurnaceRecipe source) {
		super(id);
		this.source = source;
	}
	
	public boolean matches(ItemStack stack) {
		return source.getIngredients().get(0).test(stack);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return IRecipeSerializer.SMELTING_RECIPE;
	}

	@Override
	public IRecipeType<?> getType() {
		return IRecipeType.SMELTING;
	}

	public Ingredient getIngredient() {
		return source.getIngredients().get(0);
	}
	
	public ItemStack getResult() {
		return source.assemble(DummyInventory.getInstance());
	}
	
	public int getCookTime() {
		return source.getCookingTime();
	}
	
	public float getXp() {
		return source.getExperience();
	}
	
}
