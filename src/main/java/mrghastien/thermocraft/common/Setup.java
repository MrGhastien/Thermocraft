package mrghastien.thermocraft.common;

import mrghastien.thermocraft.client.models.HeatTransmitterLoader;
import mrghastien.thermocraft.client.renderers.HeatConvectorRenderer;
import mrghastien.thermocraft.client.renderers.ThermalCapacitorRenderer;
import mrghastien.thermocraft.client.screens.*;
import mrghastien.thermocraft.common.capabilities.Capabilities;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.network.NetworkHandler;
import mrghastien.thermocraft.common.network.packets.PacketHandler;
import mrghastien.thermocraft.common.registries.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

class Setup {

    public static void init() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(Setup::setup);
        bus.addListener(Setup.Client::setup);
        bus.addListener(Setup.Server::setup);
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModTileEntities.TILES.register(bus);
        ModContainers.CONTAINERS.register(bus);
        ModRecipeSerializers.SERIALIZERS.register(bus);
        ModFluids.FLUIDS.register(bus);

        MinecraftForge.EVENT_BUS.addListener(HeatNetworkHandler.instance()::onWorldTick);
        MinecraftForge.EVENT_BUS.addListener(HeatNetworkHandler.instance()::onWorldUnload);
        MinecraftForge.EVENT_BUS.addListener(NetworkHandler.getInstance(LogicalSide.SERVER)::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(NetworkHandler.getInstance(LogicalSide.SERVER)::onWorldUnload);
        MinecraftForge.EVENT_BUS.addListener(NetworkHandler.getInstance(LogicalSide.CLIENT)::onWorldUnload);

    }

    public static void setup(final FMLCommonSetupEvent e) {
        PacketHandler.registerNetworkPackets();
        Capabilities.registerAll();
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ThermoCraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class Client {

        private static void setup(final FMLClientSetupEvent e) {
            ScreenManager.register(ModContainers.SOLID_HEATER.get(), SolidHeaterScreen::new);
            ScreenManager.register(ModContainers.BOILER.get(), BoilerScreen::new);
            ScreenManager.register(ModContainers.THERMAL_CAPACITOR.get(), ThermalCapacitorScreen::new);
            ScreenManager.register(ModContainers.FLUID_INJECTOR.get(), FluidInjectorScreen::new);

            ScreenManager.register(ModContainers.HEAT_CONVECTOR_PUMP.get(), HeatConvectorPumpScreen::new);

            //TERs
            ClientRegistry.bindTileEntityRenderer(ModTileEntities.THERMAL_CAPACITOR.get(), ThermalCapacitorRenderer::new);
            ClientRegistry.bindTileEntityRenderer(ModTileEntities.HEAT_CONVECTOR.get(), HeatConvectorRenderer::new);

            //Block render layers
            RenderTypeLookup.setRenderLayer(ModBlocks.HEAT_CONVECTOR_BLOCK.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.HEAT_CONVECTOR_PUMP.get(), RenderType.cutout());

            RenderTypeLookup.setRenderLayer(ModFluids.ETHER_OF_SADNESS.getSource(), RenderType.translucent());
            RenderTypeLookup.setRenderLayer(ModFluids.ETHER_OF_SADNESS.getFlowing(), RenderType.translucent());

        }

        @SubscribeEvent
        public static void onTextureStitch(TextureStitchEvent.Pre e) {
            if(!e.getMap().location().equals(PlayerContainer.BLOCK_ATLAS)) return;
            e.addSprite(ThermalCapacitorRenderer.TEXTURE);
        }

        @SubscribeEvent
        public static void onModelRegistering(ModelRegistryEvent e) {
            //Model Loaders
            ModelLoaderRegistry.registerLoader(ThermoCraft.modLoc("heat_transmitter_loader"), new HeatTransmitterLoader());
        }
    }

    static class Server {

        private static void setup(final FMLDedicatedServerSetupEvent e) {

        }

    }

}
