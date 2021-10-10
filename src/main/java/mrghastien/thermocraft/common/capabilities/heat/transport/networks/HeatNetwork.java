package mrghastien.thermocraft.common.capabilities.heat.transport.networks;

import mrghastien.thermocraft.api.heat.IHeatHandler;
import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.common.capabilities.Capabilities;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Cable;
import mrghastien.thermocraft.common.network.NetworkDataType;
import mrghastien.thermocraft.common.network.NetworkHandler;
import mrghastien.thermocraft.common.tileentities.cables.HeatTransmitterTile;
import mrghastien.thermocraft.util.Constants;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.*;

public abstract class HeatNetwork implements IHeatHandler{

    protected boolean needsRefresh;

    private double heatCapacity;
    private long internalEnergy;
    private double conductionCoefficient;
    private double insulationCoefficient;

    protected boolean canWork = true;
    protected boolean valid = true;

    final LazyOptional<IHeatHandler> lazy;
    protected final World world;
    final Map<BlockPos, Cable> cables;
    final Map<BlockPos, Cable> refreshMap;
    final Map<BlockPos, TransferPoint> nodes;
    protected int receiverCount;
    final long id;

    protected HeatNetwork(long id, World world) {
        this.world = world;
        this.nodes = new HashMap<>();
        this.cables = new HashMap<>();
        this.refreshMap = new HashMap<>();
        this.heatCapacity = 1000;
        this.conductionCoefficient = 40;
        this.insulationCoefficient = 0;
        this.receiverCount = 0;
        internalEnergy = (long) (heatCapacity * IHeatHandler.AIR_TEMPERATURE);
        this.lazy = LazyOptional.of(() -> this);
        this.id = id;

        NetworkHandler netHandler = NetworkHandler.getInstance(world);
        PacketDistributor.PacketTarget target = PacketDistributor.DIMENSION.with(world::dimension);
        //Not using setters to avoid triggering "onChanged"
        netHandler.add(NetworkDataType.LONG, target, this, this::getInternalEnergy, v -> internalEnergy = (long) v);
        netHandler.add(NetworkDataType.DOUBLE, target, this, this::getHeatCapacity, v -> heatCapacity = (double) v);
        netHandler.add(NetworkDataType.DOUBLE, target, this, this::getConductionCoefficient, v -> conductionCoefficient = (double) v);
        netHandler.add(NetworkDataType.DOUBLE, target, this, this::getInsulationCoefficient, v -> insulationCoefficient = (double) v);
        netHandler.add(NetworkDataType.BOOLEAN, target, this, this::canWork, v -> canWork = (boolean) v);

        //"Cable" placed : Creates a new network, or if there is another cable block nearby, register itself on this network instead.
        //Cable updated (other cable is placed nearby) : Tell the network to refresh the current block position.
        //
        //First : Create the network, and add positions only.
        //When something tries to interact with the network, "build" it by creating all "interfaces" between the network and the consumers / producers.
        //If the network is already built, refresh nodes that need to be refreshed.
        //Can handle adding positions dynamically, only add positions first.
        //Then check if there are any un-built positions, and build them.
        //WHEN BUILDING : Try to create nodes to each position, success only if there is a TE which handles heat on any side.
    }

    protected abstract void pushEnergyOut();

    void addPosition(Cable cable) {
        this.cables.put(cable.getPos(), cable);
        requestRefresh(cable.getPos(), cable);
    }

    boolean remove(BlockPos pos) {
        Cable removed = this.cables.remove(pos);
        return removed != null;
    }

    public Set<BlockPos> getCablePositions() {
        return cables.keySet();
    }

    public abstract HeatNetworkHandler.HeatNetworkType type();

    public boolean contains(BlockPos pos) {
        return cables.containsKey(pos);
    }

    public boolean isEmpty() {
        return cables.isEmpty();
    }

    public abstract int size();

    abstract boolean canMerge(HeatNetwork other);

    void requestNearbyRefresh(BlockPos pos) {
        for(Direction dir : Constants.DIRECTIONS) requestRefresh(pos.relative(dir));
    }

    void requestRefresh(BlockPos pos) {
        requestRefresh(pos, cables.get(pos));
    }

    abstract void requestRefresh(BlockPos pos, Cable cable);

    protected void refresh() {
    }

    public boolean isClientSide() {
        return world.isClientSide();
    }

    public Cable getCable(BlockPos pos) {
        return cables.get(pos);
    }

    public void tick() {
        //Send heat to connected blocks
        if(needsRefresh && nodes.size() > 0) {
            refresh();
            needsRefresh = false;
        }
        if(canWork)
            pushEnergyOut();
    }

    protected TransferPoint refreshTransferPoint(BlockPos pos, Cable c) {
        boolean hasConnections = c.hasTransferConnections();
        TransferPoint n = nodes.get(pos);
        if(n == null) {
            if(!hasConnections) return null;
            n = new TransferPoint(c);
            nodes.put(pos, n);
        } else {
            if(!hasConnections) {
                nodes.remove(pos).invalidate();
                return null;
            }
            n.recheckConnections();
        }
        return n;
    }

    public LazyOptional<IHeatHandler> getLazy() {
        return lazy;
    }

    void invalidate() {
        valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean canWork() {
        return canWork;
    }

    public World getWorld() {
        return world;
    }

    public long getId() {
        return id;
    }

    @Override
    public void onChanged() {
        //NetworkHandler.getInstance(world).sendManual(this);
    }

    @Override
    public String toString() {
        return "HeatNetwork{" +
                "id=" + id +
                ", type=" + type() +
                '}';
    }

    //================== HeatHandler Implementation ==================
    @Override
    public double getTemperature() {
        return internalEnergy / getHeatCapacity();
    }

    @Override
    public long getInternalEnergy() {
        return internalEnergy;
    }

    @Override
    public void setTemperature(double temperature) {
        setInternalEnergy((long) (temperature * getHeatCapacity()));
    }

    @Override
    public void setInternalEnergy(long energy) {
        this.internalEnergy = energy;
        if(internalEnergy < 0) internalEnergy = 0;
        onChanged();
    }

    @Override
    public double getHeatCapacity() {
        return heatCapacity;
    }

    @Override
    public void setHeatCapacity(double capacity, boolean updateEnergy) {
        if(updateEnergy) {
            setInternalEnergy((long) (getInternalEnergy() + (capacity - getHeatCapacity()) * IHeatHandler.AIR_TEMPERATURE));
        } else onChanged();
        this.heatCapacity = capacity;
    }

    @Override
    public double getConductionCoefficient() {
        return conductionCoefficient;
    }

    @Override
    public void setConductionCoefficient(double conductionCoefficient) {
        this.conductionCoefficient = conductionCoefficient;
    }

    @Override
    public double getInsulationCoefficient() {
        return insulationCoefficient;
    }

    @Override
    public void setInsulationCoefficient(double insulationCoefficient) {
        this.insulationCoefficient = insulationCoefficient;
    }

    public void transferEnergy(long energy) {
        setInternalEnergy(internalEnergy + energy);
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("internalEnergy", getInternalEnergy());
        nbt.putDouble("heatCapacity", getHeatCapacity());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        setInternalEnergy(nbt.getLong("internalEnergy"));
        setHeatCapacity(nbt.getDouble("heatCapacity"), false);
    }

    public class TransferPoint {

        protected final EnumMap<Direction, LazyOptional<IHeatHandler>> connectedHandlers;
        protected final EnumMap<Direction, LazyOptional<IHeatHandler>> receivers;
        private TransferType globalTransferType;
        protected final Cable cable;

        protected TransferPoint(Cable cable) {
            this.connectedHandlers = new EnumMap<>(Direction.class);
            this.receivers = new EnumMap<>(Direction.class);
            this.cable = cable;
            recheckConnections();
        }

        protected void recheckConnections() {
            this.globalTransferType = TransferType.NONE;
            for (Direction dir : cable.getCableConnections()) {
                globalTransferType.or(cable.getConnections().get(dir));
                TileEntity tile = world.getBlockEntity(cable.getPos());
                if (tile != null && !(tile instanceof HeatTransmitterTile)) {
                    LazyOptional<IHeatHandler> handler = tile.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, dir.getOpposite());
                    if(handler.isPresent()) {
                        TransferType type = handler.map(h -> TransferType.get(h.canReceive(), h.canExtract())).orElse(TransferType.NONE);
                        if(type.canTransfer()) {
                            handler.addListener(lazy -> invalidateConnection(dir));
                            connectedHandlers.put(dir, handler);
                            if(type.canReceive()) {
                                receivers.put(dir, handler);
                                receiverCount++;
                            }
                        }
                    }
                }
            }
        }

        protected void pushEnergyOut(final double coefficient) {
            for(LazyOptional<IHeatHandler> lazy : receivers.values()) {
                lazy.ifPresent(h -> {
                    double finalCoefficient = (Math.min(getConductionCoefficient(), h.getConductionCoefficient()) / receiverCount) * coefficient;
                    long energy = (long) (finalCoefficient * (h.getTemperature() - getTemperature()));
                    if(energy < 0) {
                        h.transferEnergy(-energy);
                        transferEnergy(energy);
                    }
                });
            }
        }

        protected TransferType getGlobalType() {
            return globalTransferType;
        }

        protected void invalidateConnection(Direction dir) {
            connectedHandlers.remove(dir);
            if(receivers.remove(dir) != null) {
                receiverCount--;
            }
            requestRefresh(cable.getPos());
        }

        public void invalidate() {
            connectedHandlers.clear();
            receiverCount -= receivers.size();
            receivers.clear();
        }
    }
}
