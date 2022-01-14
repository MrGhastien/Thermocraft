package mrghastien.thermocraft.datagen.providers;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.registries.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {


    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ThermoCraft.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.RAW_CALORITE);
        simpleItem(ModItems.POLISHED_CALORITE_CRYSTAL);
        simpleItem(ModItems.TARTANE_CRYSTAL);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return singleTexture(item.getId().getPath(), mcLoc(ITEM_FOLDER + "/generated"), "layer0", modLoc(ITEM_FOLDER + "/" + item.getId().getPath()));
    }
}
