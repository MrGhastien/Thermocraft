package mrghastien.thermocraft.common.network;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.network.data.INetworkData;
import mrghastien.thermocraft.common.network.packets.PacketHandler;
import mrghastien.thermocraft.common.network.packets.UpdateValuePacket;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkHandler {

    private static final NetworkHandler SERVER_INSTANCE = new NetworkHandler(LogicalSide.SERVER);
    private static final NetworkHandler CLIENT_INSTANCE = new NetworkHandler(LogicalSide.CLIENT);

    private NetworkHandler(LogicalSide side) {
        this.side = side;
    }

    private final LogicalSide side;
    private final Map<Object, Map<INetworkData, PacketDistributor.PacketTarget>> accessors = new LinkedHashMap<>();
    private final Map<Object, Map<INetworkData, PacketDistributor.PacketTarget>> newAccessors = new LinkedHashMap<>();

    int nextID = 0;

    /**
     * Manual accessors are accessors that will only be sent over the network when {@code sendAllManual()} is called.
     *
     */
    private final Map<Object, Map<INetworkData, PacketDistributor.PacketTarget>> manualAccessors = new HashMap<>();

    public void add(NetworkDataType type, PacketDistributor.PacketTarget target, Object key, Supplier<Object> getter, Consumer<Object> setter) {
        Map<Object, Map<INetworkData, PacketDistributor.PacketTarget>> map;
        if(side.isClient()) map = accessors;
        else map = newAccessors;
        if(!map.containsKey(key)) {
            map.put(key, new HashMap<>());
        }
        map.get(key).put(type.createData(nextID, getter, setter), target);
        nextID++;
    }

    public void addManual(NetworkDataType type, PacketDistributor.PacketTarget target, Object key, Supplier<Object> getter, Consumer<Object> setter) {
        if(!manualAccessors.containsKey(key)) {
            manualAccessors.put(key, new HashMap<>());
        }
        manualAccessors.get(key).put(type.createData(nextID, getter, setter), target);
        nextID++;
    }

    public void remove(Object key) {
        Map<INetworkData, PacketDistributor.PacketTarget> map = accessors.remove(key);
        if(map != null)
            nextID -= map.size();
        else ThermoCraft.LOGGER.warn("Couldn't remove tracked data from " + key + " : No data tracked");
    }

    public void removeManual(Object key) {
        Map<INetworkData, PacketDistributor.PacketTarget> map = manualAccessors.remove(key);
        nextID -= map.size();
    }

    public void updateValue(Object value, int id) {
        for(Map.Entry<Object, Map<INetworkData, PacketDistributor.PacketTarget>> entry : accessors.entrySet()) {
            for(INetworkData data : entry.getValue().keySet()) {
                if(data.getId() == id) {
                    try {
                        data.set(value);
                    } catch(Exception e) {
                        ThermoCraft.LOGGER.warn("Failed to synchronize value", e);
                    }
                }
            }
        }
    }

    public void updateManualValue(Object value, int id) {
        for(Map.Entry<Object, Map<INetworkData, PacketDistributor.PacketTarget>> entry : manualAccessors.entrySet()) {
            for(INetworkData data : entry.getValue().keySet()) {
                if(data.getId() == id) data.set(value);
            }
        }
    }

    private void sendAll() {
        if(this.side.isClient()) return;
        for(Map.Entry<Object, Map<INetworkData, PacketDistributor.PacketTarget>> entry : accessors.entrySet()) {
            for(Map.Entry<INetworkData, PacketDistributor.PacketTarget> subEntry : entry.getValue().entrySet()) {
                PacketDistributor.PacketTarget target = subEntry.getValue();
                INetworkData data = subEntry.getKey();
                if (data.hasChanged())
                    PacketHandler.MAIN_CHANNEL.send(target, new UpdateValuePacket(data, data.getId(), false));
            }
        }
        for(Map.Entry<Object, Map<INetworkData, PacketDistributor.PacketTarget>> entry : newAccessors.entrySet()) {
            Object key = entry.getKey();
            Map<INetworkData, PacketDistributor.PacketTarget> map = entry.getValue();
            for(Map.Entry<INetworkData, PacketDistributor.PacketTarget> subEntry : map.entrySet()) {
                PacketDistributor.PacketTarget target = subEntry.getValue();
                INetworkData data = subEntry.getKey();
                data.update();
                PacketHandler.MAIN_CHANNEL.send(target, new UpdateValuePacket(data, data.getId(), false));
            }
            if(accessors.containsKey(key))
                accessors.get(key).putAll(map);
            else accessors.put(key, map);
        }
        newAccessors.clear();
    }

    public void sendManual(Object key) {
        for(Map.Entry<INetworkData, PacketDistributor.PacketTarget> e : manualAccessors.get(key).entrySet()) {
            INetworkData data = e.getKey();
            if(data.hasChanged()) PacketHandler.MAIN_CHANNEL.send(e.getValue(), new UpdateValuePacket(data, data.getId(), true));
        }
    }

    public void onServerTick(TickEvent.ServerTickEvent e) {
        if(e.phase != TickEvent.Phase.START) return;
        sendAll();
    }

    public void onWorldUnload(WorldEvent.Unload e) {
        accessors.clear();
        newAccessors.clear();
    }

    public static NetworkHandler getInstance(World world) {
        return world.isClientSide() ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }

    public static NetworkHandler getInstance(LogicalSide side) {
        return side.isClient() ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }
}
