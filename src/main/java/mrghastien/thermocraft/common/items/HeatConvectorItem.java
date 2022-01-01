package mrghastien.thermocraft.common.items;

import mrghastien.thermocraft.common.registries.ModBlocks;
import mrghastien.thermocraft.common.registries.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class HeatConvectorItem extends BlockItem {

    public HeatConvectorItem() {
        super(ModBlocks.HEAT_CONVECTOR_BLOCK.get(), new Item.Properties().tab(ModItems.Tabs.MAIN));
    }

    @Nonnull
    @Override
    public Component getName(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getTag();
        MutableComponent name = (MutableComponent) super.getName(itemStack);
        if(nbt == null || !nbt.contains("fluid") || nbt.getString("fluid").equals(Fluids.EMPTY.getRegistryName().getPath()))
            return name.append(" (Empty)");
        else {
            Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString("fluid")));
            return name.append(" (").append(new TranslatableComponent(f.getAttributes().getTranslationKey())).append(")");
        }
    }
}
