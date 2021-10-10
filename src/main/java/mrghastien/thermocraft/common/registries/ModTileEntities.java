package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.tileentities.BoilerTile;
import mrghastien.thermocraft.common.tileentities.FluidInjectorTile;
import mrghastien.thermocraft.common.tileentities.SolidHeaterTile;
import mrghastien.thermocraft.common.tileentities.ThermalCapacitorTile;
import mrghastien.thermocraft.common.tileentities.cables.*;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModTileEntities {

    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ThermoCraft.MODID);


    @SafeVarargs
    public static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> supplier, RegistryObject<? extends Block>... blocks) {
        return TILES.register(name, () -> TileEntityType.Builder.of(supplier, Arrays.stream(blocks).map(RegistryObject::get).toArray(Block[]::new)).build(null));
    }

    public static final RegistryObject<TileEntityType<SolidHeaterTile>> SOLID_HEATER = register("solid_heater", SolidHeaterTile::new, ModBlocks.SOLID_HEATER);
    public static final RegistryObject<TileEntityType<BoilerTile>> BOILER = register("boiler", BoilerTile::new, ModBlocks.BOILER);
    public static final RegistryObject<TileEntityType<ThermalCapacitorTile>> THERMAL_CAPACITOR = register("thermal_capacitor", ThermalCapacitorTile::new, ModBlocks.THERMAL_CAPACITOR);
    public static final RegistryObject<TileEntityType<FluidInjectorTile>> FLUID_INJECTOR = register("fluid_injector", FluidInjectorTile::new, ModBlocks.FLUID_INJECTOR);

    //Transmitters
    public static final RegistryObject<TileEntityType<HeatConvectorPumpTile>> HEAT_CONVECTOR_PUMP = register("heat_convector_pump", HeatConvectorPumpTile::new, ModBlocks.HEAT_CONVECTOR_PUMP);
    public static final RegistryObject<TileEntityType<HeatConductorTile>> HEAT_CONDUCTOR = register("heat_conductor", HeatConductorTile::new, ModBlocks.HEAT_CONDUCTOR_BLOCK);
    public static final RegistryObject<TileEntityType<HeatConvectorTile>> HEAT_CONVECTOR = register("heat_convector", HeatConvectorTile::new, ModBlocks.HEAT_CONVECTOR_BLOCK);


}