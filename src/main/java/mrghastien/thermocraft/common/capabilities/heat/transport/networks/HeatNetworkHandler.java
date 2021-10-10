package mrghastien.thermocraft.common.capabilities.heat.transport.networks;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Cable;
import mrghastien.thermocraft.common.network.NetworkHandler;
import mrghastien.thermocraft.common.network.packets.CreateClientHeatNetworkPacket;
import mrghastien.thermocraft.common.network.packets.InvalidateClientHeatNetworkPacket;
import mrghastien.thermocraft.common.network.packets.PacketHandler;
import mrghastien.thermocraft.common.network.packets.UpdateCablePacket;
import mrghastien.thermocraft.util.Constants;
import mrghastien.thermocraft.util.DimPos;
import mrghastien.thermocraft.util.ModUtils;
import mrghastien.thermocraft.util.Pair;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiFunction;

/**
    This code is inspired / derived / whatever from Mekanism's system for pipes and cables.
    This isn't copy-paste-d code or the same thing but renamed, I only wrote some algorithms which roughly do what Mekanism does.
    {@link mrghastien.thermocraft.common.network.data.INetworkData}
 */
public final class  HeatNetworkHandler {

    private static final HeatNetworkHandler INSTANCE = new HeatNetworkHandler();

    private final BiMap<Long, HeatNetwork> networks = HashBiMap.create();
    private final BiMap<Long, HeatNetwork> clientNetworks = HashBiMap.create();
    private final Map<BlockPos, Cable> unassigned = new LinkedHashMap<>();
    private final Map<BlockPos, Pair<Cable, Direction>> needUpdate = new LinkedHashMap<>();
    private final Set<HeatNetwork> invalid = new LinkedHashSet<>();

    private long id;
    private long nextId() {
        return id++;
    }

    public static HeatNetworkHandler instance() {
        return INSTANCE;
    }

    public HeatNetwork get(DimPos pos, HeatNetworkType type) {
        for(HeatNetwork net : networks.values()) {
            if(net.contains(pos.blockPos()) && net.world.dimension().equals(pos.dimension()) && (net.type() == type || type == HeatNetworkType.ANY)) return net;
        }
        return null;
    }

    public HeatNetwork get(BlockPos pos, World world, HeatNetworkType type) {
        return get(new DimPos(pos, world), type);
    }

    public HeatNetwork get(long id) {
        return id == -1 ? null : networks.get(id);
    }

    public HeatNetwork getClient(long id) {
        return id == -1 ? null : clientNetworks.get(id);
    }

    public HeatNetwork create(World world, HeatNetworkType type) {
        if(world.isClientSide()) throw new IllegalArgumentException("Cannot create a client network on the server !");
        long id = nextId();
        HeatNetwork net = type.create(id, world);
        networks.put(id, net);
        PacketHandler.MAIN_CHANNEL.send(PacketDistributor.ALL.noArg(), new CreateClientHeatNetworkPacket(net));
        ThermoCraft.LOGGER.debug("Created Heat Network");
        return net;
    }

    public HeatNetwork createClient(World world, HeatNetworkType type, long id) {
        if(!world.isClientSide()) throw new IllegalArgumentException("Cannot create a server network on the client !");
        HeatNetwork net = type.create(id, world);
        clientNetworks.put(id, net);
        ThermoCraft.LOGGER.debug("Created client Heat Network");
        return net;
    }

    public HeatNetwork getOrCreate(World world, BlockPos pos, HeatNetworkType type) {
        DimPos dPos = new DimPos(pos, world);
        for(Direction ignored : Constants.DIRECTIONS) {
            HeatNetwork net = get(dPos, type);
            if(net != null) {
                return net;
            }
        }
        return create(world, type);
    }

    public HeatNetwork getOrCreate(long id, World world, HeatNetworkType type) {
        for(Direction ignored : Constants.DIRECTIONS) {
            HeatNetwork net;
            if(world.isClientSide()) net = getClient(id);
            else net = get(id);
            if(net != null) {
                return net;
            }
        }
        return create(world, type);
    }

    public void registerUnassigned(Cable c) {
        registerUnassigned(c.getPos(), c);
    }

    public void registerUnassigned(BlockPos pos, Cable c) {
        c.setNetwork(null);
        unassigned.put(pos, c);
    }

    private void updateCables() {
        for(Map.Entry<BlockPos, Pair<Cable, Direction>> entry : needUpdate.entrySet()) {
            BlockPos pos = entry.getKey();
            Cable c = entry.getValue().getFirst();
            Direction dir = entry.getValue().getSecond();
            if (c.updateDirection(dir)) {
                ThermoCraft.LOGGER.debug("Cable connection " + dir + " changed");
                sendCableChangesToClient(c, UpdateCablePacket.UpdateType.CONNECTIONS);
                if(c.hasNetwork()) c.getNetwork().requestRefresh(pos, c);
            }
        }
    }

    private void updateUnassigned() {
        Set<BlockPos> travelCache = new LinkedHashSet<>();
        for (Iterator<Cable> iterator = unassigned.values().iterator(); iterator.hasNext(); ) {
            Cable c =  iterator.next();
            HeatNetwork net = checkPath(c.getPos(), c.getWorld(), travelCache);
            addToNetwork(net, c);
            //Don't iterate over the travel cache and add each cached cable to the network because all cables either have a network or are unassigned
            //Unassigned cables are getting added to the network here, and assigned ones just get their network updated (magic of references !)
            iterator.remove();
            travelCache.clear();
        }
    }

    private HeatNetwork checkPath(final BlockPos firstPos, final World world, Set<BlockPos> travelCache) {
        final Cable initialCable = ModUtils.getCable(firstPos, world);
        LinkedHashSet<HeatNetwork> netCache = new LinkedHashSet<>();
        travelCache.add(firstPos);
        checkIntersectionPaths(initialCable, world, travelCache, netCache);
        //Don't use orElse(v) because it creates a new network even if there is no need for it
        return netCache.stream().reduce(this::mergeNetworks).orElseGet(() -> create(initialCable.getWorld(), initialCable.getType()));
    }

    private void checkIntersectionPaths(Cable cable, final World world, Set<BlockPos> travelCache, Set<HeatNetwork> netCache) {
        BlockPos pos = cable.getPos();
        for(Direction dir : cable.getCableConnections()) {
            BlockPos rel = pos.relative(dir);
            if(travelCache.contains(rel)) continue;

            Cable c = ModUtils.getCable(rel, world);
            if(c != null) {
                Cable finalCable = checkLinearCables(c, world, travelCache);
                if(finalCable != null) {
                    travelCache.add(pos);
                    if (finalCable.hasNetwork())
                        netCache.add(finalCable.getNetwork());
                        else if(finalCable.isIntersection()) checkIntersectionPaths(finalCable, world, travelCache, netCache);
                }
            }
        }
    }

    //Advances in the cable's path and returns either the first encountered intersection or null if no intersection was found (reached dead-end, already checked cable or assigned cable)
    //Always call near a cached cable
    private Cable checkLinearCables(Cable c, final World world, Set<BlockPos> travelCache) {
        BlockPos pos = c.getPos();
        Cable rel = c;
        boolean isNewCableInCache = false;
        do {
            travelCache.add(pos);
            for(Direction dir : rel.getCableConnections()) {
                BlockPos relative = pos.relative(dir);
                isNewCableInCache = travelCache.contains(relative);
                if (!isNewCableInCache) {
                    pos = relative;
                    break;
                }
            }
        } while (!(isNewCableInCache || (rel = ModUtils.getCable(pos, world)) == null || rel.hasNetwork() || rel.isIntersection())); //Exit the loop when a dead-end, an cached cable or an intersection is reached.
        return rel;
    }
    
    public void sendCableChangesToClient(Cable c, UpdateCablePacket.UpdateType updateType) {
        World world = c.getWorld();
        PacketHandler.MAIN_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(c.getPos())), new UpdateCablePacket(c, updateType));
    }
    
    private void changeCableNetwork(HeatNetwork net, @Nonnull Cable c) {
        c.setNetwork(net);
        if(!c.getWorld().isClientSide()) sendCableChangesToClient(c, UpdateCablePacket.UpdateType.NETWORK);
    }

    public void onNeighborChange(@Nonnull Cable c, Direction dir) {
        needUpdate.put(c.getPos(), new Pair<>(c, dir));
    }

    public void addToNetwork(HeatNetwork net, @Nonnull Cable c) {
        if(net != null) net.addPosition(c);
        if(c.hasNetwork()) removeFromNetwork(c.getNetwork(), c);
        changeCableNetwork(net, c);
    }

    public void removeFromNetwork(HeatNetwork net, @Nonnull Cable cable) {
        //TODO: make cable removal work (UPDATE: needs testing)
        net.remove(cable.getPos());

        if(!net.isClientSide()) {
            if (cable.isValid()) changeCableNetwork(null, cable);
            net.cables.values().forEach(c -> {
                HeatNetworkHandler.instance().registerUnassigned(c);
            });
            invalidateNetwork(net);
        }
    }
    
    private HeatNetwork mergeNetworks(@Nonnull HeatNetwork net, @Nonnull HeatNetwork other) {
        if(!(other.type() != net.type() || other == net || !net.canMerge(other))) {
            for (Map.Entry<BlockPos, Cable> e : other.cables.entrySet()) {
                Cable c = e.getValue();
                net.addPosition(c);
                changeCableNetwork(net, c);
            }
            net.setInternalEnergy(net.getInternalEnergy() + other.getInternalEnergy());
            invalidateNetwork(other);
            ThermoCraft.LOGGER.debug("Merged networks");
        }
        return net;
    }

    private HeatNetwork mergeWithNeighbors(Cable c) {
        HeatNetwork net = null;
        if(c.getCableConnections().size() > 0) {
            net = c.getNetwork();
            BlockPos currentPos = c.getPos();
            for(Direction dir : c.getCableConnections()) {
                Cable other = ModUtils.getCable(currentPos.relative(dir), c.getWorld());
                if(other != null && c.getType() == other.getType() && other.hasNetwork()) {
                    HeatNetwork otherNetwork = other.getNetwork();
                    if(net == null) net = otherNetwork;
                    else if(net != otherNetwork) mergeNetworks(net, other.getNetwork());
                }
            }
        }
        return net;
    }

    public void invalidateNetwork(HeatNetwork network) {
        if(network.isClientSide()) {
            throw new IllegalStateException();
        }

        network.invalidate();
        NetworkHandler.getInstance(network.getWorld()).remove(this);
        network.lazy.invalidate();
        network.nodes.forEach((p, n) -> n.invalidate());
        network.nodes.clear();
        network.cables.clear();
        notifyNetworkInvalidation(network);
        networks.remove(network.id);
        ThermoCraft.LOGGER.debug("Invalidated heat network");
    }

    private void notifyNetworkInvalidation(HeatNetwork net) {
        PacketHandler.MAIN_CHANNEL.send(PacketDistributor.ALL.noArg(), new InvalidateClientHeatNetworkPacket(net.id));
    }

    public void invalidateClientNetwork(HeatNetwork network) {
        clientNetworks.remove(network.id);
        ThermoCraft.LOGGER.debug("Invalidated client heat network");
    }

    public void onWorldTick(TickEvent.WorldTickEvent e) {
        if(e.phase != TickEvent.Phase.END || e.side.isClient()) return;
        for(HeatNetwork net : networks.values()) {
             net.tick();
        }

        for(HeatNetwork net : invalid) {
            invalidateNetwork(net);
        }
        invalid.clear();
        updateCables();
        updateUnassigned();
    }

    public void onWorldUnload(WorldEvent.Unload e) {
        //for(HeatNetwork n : networks.values()) invalidateNetwork(n);
        networks.clear();
        clientNetworks.clear();
        unassigned.clear();
    }

    public enum HeatNetworkType {
        CONDUCTOR(HeatConductorNetwork::new),
        CONVECTOR(HeatConvectorNetwork::new),
        ANY(null);

        private final BiFunction<Long, World, HeatNetwork> factory;

        HeatNetworkType(BiFunction<Long, World, HeatNetwork> factory) {
           this.factory = factory;
        }

        HeatNetwork create(long id, World world) {
            if(factory == null) throw new UnsupportedOperationException("Cannot create a network without a type");
            return factory.apply(id, world);
        }
    }

}
