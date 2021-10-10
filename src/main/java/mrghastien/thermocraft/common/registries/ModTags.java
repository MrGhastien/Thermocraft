package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag;
import net.minecraft.util.Util;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;

public class ModTags {

    public static class Fluids {

        public static final ITag.INamedTag<Fluid> COOLANT = ForgeTagHandler.createOptionalTag(ForgeRegistries.FLUIDS, ThermoCraft.modLoc("coolant"), Util.make(new HashSet<>(), s -> {
            s.add(() -> net.minecraft.fluid.Fluids.WATER);
            s.add(ModFluids.ETHER_OF_SADNESS::getSource);
        }));

    }

}
