package mrghastien.thermocraft.datagen;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.datagen.providers.*;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ThermoCraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper exFileHelper = event.getExistingFileHelper();
        if(event.includeServer()) {
            generator.addProvider(new ModRecipeProvider(generator));
            ModBlockTagsProvider blocks = new ModBlockTagsProvider(generator, exFileHelper);
            generator.addProvider(new ModItemTagsProvider(generator, blocks, exFileHelper));
            generator.addProvider(new ModFluidTagsProvider(generator, exFileHelper));
            generator.addProvider(blocks);
            generator.addProvider(new ModLootTableProvider(generator));
        }
        if(event.includeClient()) {
            generator.addProvider(new ModBlockStateProvider(generator, exFileHelper));
            generator.addProvider(new ModItemModelProvider(generator, exFileHelper));
        }
    }

}
