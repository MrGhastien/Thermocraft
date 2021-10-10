package mrghastien.thermocraft.datagen.providers;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.registries.ModFluids;
import mrghastien.thermocraft.common.registries.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.data.IDataProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModFluidTagsProvider extends FluidTagsProvider {

    public ModFluidTagsProvider(DataGenerator generator, ExistingFileHelper exFileHelper) {
        super(generator, ThermoCraft.MODID, exFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ModTags.Fluids.COOLANT).addOptional(ModFluids.ETHER_OF_SADNESS.getId()).addOptional(ModFluids.ETHER_OF_SADNESS.getFlowingId());
    }

    @Override
    public String getName() {
        return "Thermocraft fluid tags";
    }
}
