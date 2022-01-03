package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.crafting.BoilingRecipe;
import mrghastien.thermocraft.common.crafting.FluidInjectionRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {

   public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ThermoCraft.MODID);

   public static final RegistryObject<BoilingRecipe.Serializer> BOILING = SERIALIZERS.register("boiling", BoilingRecipe.Serializer::new);
   public static final RegistryObject<FluidInjectionRecipe.Serializer> FLUID_INJECTION = SERIALIZERS.register("fluid_injection", FluidInjectionRecipe.Serializer::new);

}
