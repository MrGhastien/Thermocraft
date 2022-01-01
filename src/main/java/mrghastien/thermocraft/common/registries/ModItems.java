package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.items.HeatConvectorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ThermoCraft.MODID);

    public static final RegistryObject<HeatConvectorItem> HEAT_CONVECTOR = ITEMS.register("heat_convector", HeatConvectorItem::new);

    public static class Tabs {
        public static final CreativeModeTab MAIN = new CreativeModeTab(ThermoCraft.MODID) {
            @Nonnull
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Items.BARREL);
            }
        };
    }

}
