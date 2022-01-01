package mrghastien.thermocraft.common.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class FurnaceRecipeWrapper extends BaseRecipe {

	private final SmeltingRecipe source;
	
	public FurnaceRecipeWrapper(ResourceLocation id, SmeltingRecipe source) {
		super(id);
		this.source = source;
	}
	
	public boolean matches(ItemStack stack) {
		return source.getIngredients().get(0).test(stack);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SMELTING_RECIPE;
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeType.SMELTING;
	}

	public Ingredient getIngredient() {
		return source.getIngredients().get(0);
	}
	
	public ItemStack getResult() {
		return source.assemble(DummyContainer.getInstance());
	}
	
	public int getCookTime() {
		return source.getCookingTime();
	}
	
	public float getXp() {
		return source.getExperience();
	}
	
}
