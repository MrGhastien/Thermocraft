package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.api.heat.IHeatHandler;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.crafting.FluidIngredient;
import mrghastien.thermocraft.common.crafting.StackIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ThermoCraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    @SubscribeEvent
    public static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> e) {
        CraftingHelper.register(new ResourceLocation(ThermoCraft.MODID, "fluid"), FluidIngredient.Serializer.INSTANCE);
        CraftingHelper.register(new ResourceLocation(ThermoCraft.MODID, "stacked_item"), StackIngredient.Serializer.INSTANCE);
    }

}
