package mrghastien.thermocraft.datagen.providers;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.registries.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, ThermoCraft.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CALORITE_ORE.getBlock())
                .add(ModBlocks.TARTANE_ORE.getBlock());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.TARTANE_ORE.getBlock());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.CALORITE_ORE.getBlock());

        tag(Tags.Blocks.ORES)
                .add(ModBlocks.TARTANE_ORE.getBlock())
                .add(ModBlocks.CALORITE_ORE.getBlock());
    }

    @Override
    public String getName() {
        return ThermoCraft.MODID + " Block Tags";
    }
}
