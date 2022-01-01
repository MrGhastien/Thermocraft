package mrghastien.thermocraft.common.registries;
import mrghastien.thermocraft.common.ThermoCraft;
import net.minecraft.Util;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;

public class ModTags {

    public static class Fluids {

        public static final Tag.Named<Fluid> COOLANT = ForgeTagHandler.createOptionalTag(ForgeRegistries.FLUIDS, ThermoCraft.modLoc("coolant"), Util.make(new HashSet<>(), s -> {
            s.add(() -> net.minecraft.world.level.material.Fluids.WATER);
            s.add(ModFluids.ETHER_OF_SADNESS::getSource);
        }));

    }

}
