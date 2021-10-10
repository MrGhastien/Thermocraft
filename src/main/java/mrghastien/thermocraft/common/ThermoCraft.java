package mrghastien.thermocraft.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ThermoCraft.MODID)
public class ThermoCraft {

    public static final String MODID = "thermocraft";
    public static final Logger LOGGER = LogManager.getLogger();

    public ThermoCraft() {
        Setup.init();
    }

    public static ResourceLocation modLoc(String name) {
        return new ResourceLocation(MODID, name);
    }
}
