package mrghastien.thermocraft.datagen.providers;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.registries.ModBlocks;
import mrghastien.thermocraft.common.registries.ModFluids;
import mrghastien.thermocraft.datagen.CriterionHelper;
import mrghastien.thermocraft.datagen.builders.recipe.BoilingRecipeBuilder;
import mrghastien.thermocraft.datagen.builders.recipe.FluidInjectionRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildShapelessRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        BoilingRecipeBuilder.boiling(Fluids.LAVA, 10, 10).ingredient(Fluids.WATER, 10).save(consumer, new ResourceLocation(ThermoCraft.MODID, "boiling/water_to_lava"));

        convectorInjectionRecipe(consumer, ModFluids.ETHER_OF_SADNESS.getSource());
        convectorInjectionRecipe(consumer, Fluids.WATER);
    }

    private void convectorInjectionRecipe(@Nonnull Consumer<IFinishedRecipe> consumer, Fluid f) {
        CompoundNBT nbt = new CompoundNBT();
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
