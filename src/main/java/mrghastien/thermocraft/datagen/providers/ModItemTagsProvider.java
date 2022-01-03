package mrghastien.thermocraft.datagen.providers;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.registries.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, pBlockTagsProvider, ThermoCraft.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(Tags.Items.ORES)
                .add(ModBlocks.TARTANE_ORE.getItem())
                .add(ModBlocks.CALORITE_ORE.getItem());
    }

    @Nonnull
    @Override
    public String getName() {
        return ThermoCraft.MODID + " Item Tags";
    }
}
