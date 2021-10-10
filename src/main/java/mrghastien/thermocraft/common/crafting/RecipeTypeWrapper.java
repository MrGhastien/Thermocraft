package mrghastien.thermocraft.common.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is used to wrap vanilla recipe types with a custom type (which extends BaseRecipe)
 * @param <T> The wrapper type
 * @param <R> The type we want to wrap
 */
public class RecipeTypeWrapper<T extends BaseRecipe, R extends IRecipe<IInventory>> extends ModRecipeType<T> {
	
	private final IRecipeType<R> base;
	private final Function<R, T> wrappedFunction;
	
	public RecipeTypeWrapper(IRecipeType<R> base, Function<R, T> wrappedFunction) {
		super(base.toString());
		this.base = base;
		this.wrappedFunction = wrappedFunction;
	}
	
	@Override
	public Map<ResourceLocation, T> getRecipes(World world) {
        if (world == null) {
            world = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD); // World.field_234918_g_ = World.OVERWORLD
            if (world == null) return Collections.emptyMap();
        }

        if (cache.isEmpty()) {
            RecipeManager recipeManager = world.getRecipeManager();
            Collection<IRecipe<?>> recipes = recipeManager.getRecipes();
            recipes.forEach(r -> {
            	if(r.getType() == base) {
            		cache.put(r.getId(), wrappedFunction.apply((R) r));
            	}
            });
        }
        return cache;
    }
}