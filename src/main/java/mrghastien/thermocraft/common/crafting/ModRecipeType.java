	package mrghastien.thermocraft.common.crafting;

    import mrghastien.thermocraft.common.ThermoCraft;
    import net.minecraft.core.Registry;
    import net.minecraft.resources.ResourceLocation;
    import net.minecraft.world.Container;
    import net.minecraft.world.item.crafting.Recipe;
    import net.minecraft.world.item.crafting.RecipeManager;
    import net.minecraft.world.item.crafting.RecipeSerializer;
    import net.minecraft.world.item.crafting.RecipeType;
    import net.minecraft.world.level.Level;
    import net.minecraftforge.registries.IForgeRegistry;
    import net.minecraftforge.server.ServerLifecycleHooks;

    import java.util.*;
    import java.util.function.Function;
    import java.util.function.Predicate;
    import java.util.stream.Stream;

    public class ModRecipeType<T extends BaseRecipe> implements RecipeType<T> {

        private static final List<ModRecipeType<? extends BaseRecipe>> types = new ArrayList<>();
        private static final List<ModRecipeType<? extends BaseRecipe>> wrappers = new ArrayList<>();

        //Custom Types
        public static final ModRecipeType<BoilingRecipe> BOILING = register("boiling");
        public static final ModRecipeType<FluidInjectionRecipe> FLUID_INJECTION = register("fluid_injection");

        //Wrapper Types
        //public static final RecipeTypeWrapper<FurnaceRecipeWrapper, FurnaceRecipe> SMELTING_WRAPPER = register(IRecipeType.SMELTING, r -> new FurnaceRecipeWrapper(r.getId(), r));

        private static <T extends BaseRecipe> ModRecipeType<T> register(String name) {
            ModRecipeType<T> type = new ModRecipeType<>(name);
            types.add(type);
            return type;
        }

        /**Used to register vanilla recipe wrappers.
         *
         * @param <T> The wrapper type
         * @param <R> The base recipe type
         * @param type The {@link RecipeType} corresponding to the vanilla recipe
         * @param func A function converting the vanilla recipe to a wrapper
         */
        private static <T extends BaseRecipe, R extends Recipe<Container>> RecipeTypeWrapper<T, R> register(RecipeType<R> type, Function<R, T> func) {
            RecipeTypeWrapper<T, R> wrapper = new RecipeTypeWrapper<>(type, func);
            wrappers.add(wrapper);
            return wrapper;
        }

        public static void registerTypes(IForgeRegistry<RecipeSerializer<?>> registry) {
            types.forEach(type -> Registry.register(Registry.RECIPE_TYPE, type.registryName, type));
        }

        private final ResourceLocation registryName;
        final Map<ResourceLocation, T> cache = new HashMap<>();

        protected ModRecipeType(String name) {
            this.registryName = new ResourceLocation(ThermoCraft.MODID, name);
        }

        public Map<ResourceLocation, T> getRecipes(Level world) {
            if (world == null) {
                world = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
                if (world == null) return Collections.emptyMap();
            }

            if (cache.isEmpty()) {
                RecipeManager recipeManager = world.getRecipeManager();
                List<T> recipes = recipeManager.getRecipesFor(this, BaseRecipe.DummyContainer.getInstance(), world);
                recipes.forEach(recipe -> cache.put(recipe.getId(), recipe));
            }
            return cache;
        }

        public Stream<T> stream(Level world) {
            return getRecipes(world).values().stream();
        }

        public boolean anyMatch(Level level, Predicate<T> predicate) {
            return stream(level).anyMatch(predicate);
        }
    }
