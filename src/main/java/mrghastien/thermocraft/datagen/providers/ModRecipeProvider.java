package mrghastien.thermocraft.datagen.providers;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.registries.ModBlocks;
import mrghastien.thermocraft.common.registries.ModFluids;
import mrghastien.thermocraft.datagen.CriterionHelper;
import mrghastien.thermocraft.datagen.builders.recipe.BoilingRecipeBuilder;
import mrghastien.thermocraft.datagen.builders.recipe.FluidInjectionRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
        BoilingRecipeBuilder.boiling(Fluids.LAVA, 10, 10).ingredient(Fluids.WATER, 10).save(consumer, new ResourceLocation(ThermoCraft.MODID, "boiling/water_to_lava"));

        convectorInjectionRecipe(consumer, ModFluids.ETHER_OF_SADNESS.getSource());
        convectorInjectionRecipe(consumer, Fluids.WATER);
    }

    private void convectorInjectionRecipe(@Nonnull Consumer<FinishedRecipe> consumer, Fluid f) {
        CompoundTag nbt = new CompoundTag();
        ResourceLocation name = f.getRegistryName();
        nbt.putString("fluid", name.toString());
        Item heatConvectorItem = ModBlocks.HEAT_CONVECTOR_BLOCK.get().asItem();
        ItemStack inputStack = new ItemStack(heatConvectorItem, 1);
        inputStack.setTag(null);
        ItemStack resultStack = new ItemStack(heatConvectorItem, 1);
        resultStack.setTag(nbt);
        FluidInjectionRecipeBuilder.recipe(new FluidStack(f, 250), inputStack, resultStack).unlockedBy("placed_injector", CriterionHelper.placedBlock(ModBlocks.FLUID_INJECTOR.get())).save(consumer,  ThermoCraft.modLoc("fluid_injection/" + name.getPath() + "_heat_convector"));
    }
}
