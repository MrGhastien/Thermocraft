package mrghastien.thermocraft.common;

import mrghastien.thermocraft.client.models.HeatTransmitterLoader;
import mrghastien.thermocraft.client.renderers.BoilerRenderer;
import mrghastien.thermocraft.client.renderers.HeatConvectorRenderer;
import mrghastien.thermocraft.client.renderers.TartanicHolderRenderer;
import mrghastien.thermocraft.client.renderers.ThermalCapacitorRenderer;
import mrghastien.thermocraft.client.screens.*;
import mrghastien.thermocraft.common.capabilities.Capabilities;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.inventory.menus.BaseMenu;
import mrghastien.thermocraft.common.network.packets.PacketHandler;
import mrghastien.thermocraft.common.registries.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

class Setup {

    public static void init() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        //Other setup events
        bus.addListener(Setup::setup);
        bus.addListener(Setup.Client::setup);
        bus.addListener(Setup.Server::setup);

        //Game objects
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModTileEntities.TILES.register(bus);
        ModMenus.MENUS.register(bus);
        ModRecipeSerializers.SERIALIZERS.register(bus);
        ModFluids.FLUIDS.register(bus);

        bus.addListener(Capabilities::registerCapabilities);

        //Heat Network events
        forgeBus.addListener(HeatNetworkHandler.instance()::onWorldTick);
        forgeBus.addListener(HeatNetworkHandler.instance()::onWorldUnload);
        //MinecraftForge.EVENT_BUS.addListener(NetworkHandler.getInstance(LogicalSide.SERVER)::onServerTick);
        //MinecraftForge.EVENT_BUS.addListener(NetworkHandler.getInstance(LogicalSide.SERVER)::onWorldUnload);
        //MinecraftForge.EVENT_BUS.addListener(NetworkHandler.getInstance(LogicalSide.CLIENT)::onWorldUnload);

        //Game events
        forgeBus.addListener(BaseMenu::onContainerClosedByPlayer);
        forgeBus.addListener(BaseMenu::onContainerOpenedByPlayer);
    }

    public static void setup(final FMLCommonSetupEvent e) {
        PacketHandler.registerNetworkPackets();
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ThermoCraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class Client {

        private static void setup(final FMLClientSetupEvent e) {
            MenuScreens.register(ModMenus.SOLID_HEATER.get(), SolidHeaterScreen::new);
            MenuScreens.register(ModMenus.BOILER.get(), BoilerScreen::new);
            MenuScreens.register(ModMenus.THERMAL_CAPACITOR.get(), ThermalCapacitorScreen::new);
            MenuScreens.register(ModMenus.FLUID_INJECTOR.get(), FluidInjectorScreen::new);

            MenuScreens.register(ModMenus.HEAT_CONVECTOR_PUMP.get(), HeatConvectorPumpScreen::new);

            //Block entity renderers
            BlockEntityRenderers.register(ModTileEntities.THERMAL_CAPACITOR.get(), ThermalCapacitorRenderer::new);
            BlockEntityRenderers.register(ModTileEntities.HEAT_CONVECTOR.get(), HeatConvectorRenderer::new);
            BlockEntityRenderers.register(ModTileEntities.BOILER.get(), BoilerRenderer::new);
            BlockEntityRenderers.register(ModTileEntities.TARTANIC_HOLDER.get(), TartanicHolderRenderer::new);

            //Block render layers
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAT_CONVECTOR_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAT_CONVECTOR_PUMP.getBlock(), RenderType.cutout());

            ItemBlockRenderTypes.setRenderLayer(ModFluids.ETHER_OF_SADNESS.getSource(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.ETHER_OF_SADNESS.getFlowing(), RenderType.translucent());

        }

        @SubscribeEvent
        public static void onTextureStitch(TextureStitchEvent.Pre e) {
            if(!e.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) return;
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
