package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.tileentities.BoilerTile;
import mrghastien.thermocraft.common.tileentities.FluidInjectorTile;
import mrghastien.thermocraft.common.tileentities.SolidHeaterTile;
import mrghastien.thermocraft.common.tileentities.ThermalCapacitorTile;
import mrghastien.thermocraft.common.tileentities.cables.HeatConductorTile;
import mrghastien.thermocraft.common.tileentities.cables.HeatConvectorPumpTile;
import mrghastien.thermocraft.common.tileentities.cables.HeatConvectorTile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModTileEntities {

    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ThermoCraft.MODID);


    @SafeVarargs
    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> supplier, RegistryObject<? extends Block>... blocks) {
        return TILES.register(name, () -> BlockEntityType.Builder.of(supplier, Arrays.stream(blocks).map(RegistryObject::get).toArray(Block[]::new)).build(null));
    }

    public static final RegistryObject<BlockEntityType<SolidHeaterTile>> SOLID_HEATER = register("solid_heater", SolidHeaterTile::new, ModBlocks.SOLID_HEATER);
    public static final RegistryObject<BlockEntityType<BoilerTile>> BOILER = register("boiler", BoilerTile::new, ModBlocks.BOILER);
    public static final RegistryObject<BlockEntityType<ThermalCapacitorTile>> THERMAL_CAPACITOR = register("thermal_capacitor", ThermalCapacitorTile::new, ModBlocks.THERMAL_CAPACITOR);
    public static final RegistryObject<BlockEntityType<FluidInjectorTile>> FLUID_INJECTOR = register("fluid_injector", FluidInjectorTile::new, ModBlocks.FLUID_INJECTOR);

    //Transmitters
    public static final RegistryObject<BlockEntityType<HeatConvectorPumpTile>> HEAT_CONVECTOR_PUMP = register("heat_convector_pump", HeatConvectorPumpTile::new, ModBlocks.HEAT_CONVECTOR_PUMP);
    public static final RegistryObject<BlockEntityType<HeatConductorTile>> HEAT_CONDUCTOR = register("heat_conductor", HeatConductorTile::new, ModBlocks.HEAT_CONDUCTOR_BLOCK);
    public static final RegistryObject<BlockEntityType<HeatConvectorTile>> HEAT_CONVECTOR = register("heat_convector", HeatConvectorTile::new, ModBlocks.HEAT_CONVECTOR_BLOCK);


}