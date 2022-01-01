package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.blocks.machines.boiler.BoilerBlockEntity;
import mrghastien.thermocraft.common.blocks.machines.fluidinjector.FluidInjectorBlockEntity;
import mrghastien.thermocraft.common.blocks.machines.solidheater.SolidHeaterBlockEntity;
import mrghastien.thermocraft.common.blocks.machines.thermalcapacitor.ThermalCapacitorBlockEntity;
import mrghastien.thermocraft.common.blocks.transmitters.conductor.HeatConductorBlockEntity;
import mrghastien.thermocraft.common.blocks.transmitters.convector.HeatConvectorBlockEntity;
import mrghastien.thermocraft.common.blocks.transmitters.convector.HeatConvectorPumpBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;

public class ModTileEntities {

    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ThermoCraft.MODID);


    @SafeVarargs
    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> supplier, RegistryObject<? extends Block>... blocks) {
        return TILES.register(name, () -> BlockEntityType.Builder.of(supplier, Arrays.stream(blocks).map(RegistryObject::get).toArray(Block[]::new)).build(null));
    }

    public static final RegistryObject<BlockEntityType<SolidHeaterBlockEntity>> SOLID_HEATER = register("solid_heater", SolidHeaterBlockEntity::new, ModBlocks.SOLID_HEATER);
    public static final RegistryObject<BlockEntityType<BoilerBlockEntity>> BOILER = register("boiler", BoilerBlockEntity::new, ModBlocks.BOILER);
    public static final RegistryObject<BlockEntityType<ThermalCapacitorBlockEntity>> THERMAL_CAPACITOR = register("thermal_capacitor", ThermalCapacitorBlockEntity::new, ModBlocks.THERMAL_CAPACITOR);
    public static final RegistryObject<BlockEntityType<FluidInjectorBlockEntity>> FLUID_INJECTOR = register("fluid_injector", FluidInjectorBlockEntity::new, ModBlocks.FLUID_INJECTOR);

    //Transmitters
    public static final RegistryObject<BlockEntityType<HeatConvectorPumpBlockEntity>> HEAT_CONVECTOR_PUMP = register("heat_convector_pump", HeatConvectorPumpBlockEntity::new, ModBlocks.HEAT_CONVECTOR_PUMP);
    public static final RegistryObject<BlockEntityType<HeatConductorBlockEntity>> HEAT_CONDUCTOR = register("heat_conductor", HeatConductorBlockEntity::new, ModBlocks.HEAT_CONDUCTOR_BLOCK);
    public static final RegistryObject<BlockEntityType<HeatConvectorBlockEntity>> HEAT_CONVECTOR = register("heat_convector", HeatConvectorBlockEntity::new, ModBlocks.HEAT_CONVECTOR_BLOCK);


}