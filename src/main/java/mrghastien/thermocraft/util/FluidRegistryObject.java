package mrghastien.thermocraft.util;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.registries.ModBlocks;
import mrghastien.thermocraft.common.registries.ModFluids;
import mrghastien.thermocraft.common.registries.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.UnaryOperator;

public class FluidRegistryObject {

    private final RegistryObject<ForgeFlowingFluid.Source> source;
    private final RegistryObject<ForgeFlowingFluid.Flowing> flowing;
    private final RegistryObject<FlowingFluidBlock> block;
    private final RegistryObject<BucketItem> bucket;

    private final ResourceLocation id;

    private FluidRegistryObject(String name, UnaryOperator<FluidAttributes.Builder> attributes, UnaryOperator<ForgeFlowingFluid.Properties> propertiesOperator) {

        FluidAttributes.Builder attributesBuilder = attributes.apply(FluidAttributes.builder(ThermoCraft.modLoc("block/" + name + "_still"), ThermoCraft.modLoc("block/" + name + "_flowing")));
        ForgeFlowingFluid.Properties properties = propertiesOperator.apply(new ForgeFlowingFluid.Properties(this::getSource, this::getFlowing, attributesBuilder).bucket(this::getBucket).block(this::getBlock));

        this.source = ModFluids.FLUIDS.register(name, () -> new ForgeFlowingFluid.Source(properties));
        this.flowing = ModFluids.FLUIDS.register("flowing_" + name, () -> new ForgeFlowingFluid.Flowing(properties));
        this.block = ModBlocks.BLOCKS.register(name, () -> new FlowingFluidBlock(this::getSource, AbstractBlock.Properties.of(Material.WATER, MaterialColor.COLOR_PURPLE).noCollission().strength(100.0F).noDrops()));
        this.bucket = ModItems.ITEMS.register(name + "_bucket", () -> new BucketItem(this::getSource, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ModItems.Tabs.MAIN)));
        this.id = ThermoCraft.modLoc(name);
    }

    public static FluidRegistryObject of(String name, UnaryOperator<FluidAttributes.Builder> attributes, UnaryOperator<ForgeFlowingFluid.Properties> propertiesOperator) {
        return new FluidRegistryObject(name, attributes, propertiesOperator);
    }

    public ForgeFlowingFluid.Source getSource() {
        return source.get();
    }

    public ForgeFlowingFluid.Flowing getFlowing() {
        return flowing.get();
    }

    public FlowingFluidBlock getBlock() {
        return block.get();
    }

    public BucketItem getBucket() {
        return bucket.get();
    }

    public ResourceLocation getId() {
        return id;
    }

    public ResourceLocation getFlowingId() {
        return ThermoCraft.modLoc("flowing_" + id.getPath());
    }
}
