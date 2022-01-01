package mrghastien.thermocraft.common.capabilities;

import mrghastien.thermocraft.api.heat.IHeatHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class Capabilities {

    public static Capability<IHeatHandler> HEAT_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static void registerCapabilities(RegisterCapabilitiesEvent e) {
        e.register(IHeatHandler.class);
    }
}
