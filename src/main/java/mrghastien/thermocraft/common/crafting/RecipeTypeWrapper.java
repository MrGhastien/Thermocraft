package mrghastien.thermocraft.common.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is used to wrap vanilla recipe types with a custom type (which extends BaseRecipe)
 * @param <T> The wrapper type
 * @param <R> The type we want to wrap
 */
public class RecipeTypeWrapper<T extends BaseRecipe, R extends Recipe<Container>> extends ModRecipeType<T> {
	
	private final RecipeType<R> base;
	private final Function<R, T> wrappedFunction;
	
	public RecipeTypeWrapper(RecipeType<R> base, Function<R, T> wrappedFunction) {
		super(base.toString());
		this.base = base;
		this.wrappedFunction = wrappedFunction;
	}
	
	@Override
	public Map<ResourceLocation, T> getRecipes(Level world) {
        if (world == null) {
            world = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD); // World.field_234918_g_ = World.OVERWORLD
            if (world == null) return Collections.emptyMap();
        }

        if (cache.isEmpty()) {
            RecipeManager recipeManager = world.getRecipeManager();
            Collection<Recipe<?>> recipes = recipeManager.getRecipes();
            recipes.forEach(r -> {
            	if(r.getType() == base) {
            		cache.put(r.getId(), wrappedFunction.apply((R) r));
            	}
            });
        }
        return cache;
    }
}