package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.util.FluidRegistryObject;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModFluids {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ThermoCraft.MODID);

    public static final FluidRegistryObject ETHER_OF_SADNESS = FluidRegistryObject.of("ether_of_sadness", a -> a.density(2500).viscosity(2500), p -> p.tickRate(15));

}