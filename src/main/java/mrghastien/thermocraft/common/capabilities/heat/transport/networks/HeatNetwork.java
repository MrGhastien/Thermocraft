package mrghastien.thermocraft.common.capabilities.heat.transport.networks;

import mrghastien.thermocraft.api.heat.IHeatHandler;
import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.common.capabilities.Capabilities;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Cable;
import mrghastien.thermocraft.common.network.CompositeDataNetworkBinding;
import mrghastien.thermocraft.common.network.INetworkBinding;
import mrghastien.thermocraft.common.network.data.DataReference;
import mrghastien.thermocraft.common.network.data.DataType;
import mrghastien.thermocraft.common.network.data.IDataHolder;
import mrghastien.thermocraft.common.network.packets.PacketHandler;
import mrghastien.thermocraft.common.network.packets.UpdateHeatNetworkPacket;
import mrghastien.thermocraft.common.tileentities.cables.HeatTransmitterTile;
import mrghastien.thermocraft.util.Constants;
import mrghastien.thermocraft.util.math.FixedPointNumber;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class HeatNetwork implements IHeatHandler{

    protected boolean needsRefresh;

    private double heatCapacity;
    private FixedPointNumber.Mutable internalEnergy;
    private double conductionCoefficient;
    private double insulationCoefficient;

    protected boolean canWork = true;
    protected boolean valid = true;

    final LazyOptional<IHeatHandler> lazy;
    protected final Level world;
    final Map<BlockPos, Cable> cables;
    final Map<BlockPos, Cable> refreshMap;
    final Map<BlockPos, TransferPoint> nodes;
    final Set<LevelChunk> chunks;
    protected int receiverCount;
    final long id;

    protected final IDataHolder dataHolder;

    protected HeatNetwork(long id, Level world) {
        this.world = world;
        this.nodes = new HashMap<>();
        this.cables = new HashMap<>();
        this.refreshMap = new HashMap<>();
        this.chunks = new HashSet<>();
        this.heatCapacity = 1000;
        this.conductionCoefficient = 40;
        this.insulationCoefficient = 0;
        this.receiverCount = 0;
        this.internalEnergy = FixedPointNumber.Mutable.valueOf(heatCapacity * IHeatHandler.AIR_TEMPERATURE);
        this.lazy = LazyOptional.of(() -> this);
        this.id = id;
        this.dataHolder = new DataHolder();

        dataHolder.addData(DataType.FIXED_POINT, "internal_energy_" + id, this::getInternalEnergy, this::setInternalEnergy);
        dataHolder.addData(DataType.DOUBLE, "heat_capacity_" + id, this::getHeatCapacity, v -> this.setHeatCapacity(v, false));
        dataHolder.addData(DataType.DOUBLE, "conduction_" + id, this::getConductionCoefficient, this::setConductionCoefficient);
        dataHolder.addData(DataType.DOUBLE, "insulation_" + id, this::getInsulationCoefficient, this::setInsulationCoefficient);
        dataHolder.addData(DataType.BOOL, "can_work_" + id, this::canWork, v -> canWork = v);

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
        chunks.add(world.getChunkAt(cable.getPos()));
        requestRefresh(cable.getPos(), cable);
    }

    boolean remove(BlockPos pos) {
        Cable removed = this.cables.remove(pos);
        if(removed == null) return false;
        boolean canRemove = true;
        LevelChunk c = world.getChunkAt(pos);
        for(BlockPos p : c.getBlockEntitiesPos()) {
            if(HeatNetworkHandler.instance().get(pos, world, type()) == this) {
                canRemove = false;
                break;
            }
        }
        if(canRemove) chunks.remove(c);
        return true;
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

    public abstract void requestRefresh(BlockPos pos, Cable cable);

    protected abstract void refresh();

    public boolean isClientSide() {
        return world.isClientSide();
    }

    public Cable getCable(BlockPos pos) {
        return cables.get(pos);
    }

    public void tick() {
        //Send heat to connected blocks
        if(needsRefresh) {
            refresh();
            needsRefresh = false;
        }
        if(canWork)
            pushEnergyOut();
    }

    void broadcastChanges() {
        if(dataHolder.getBinding().hasChanged()) {
            PacketHandler.MAIN_CHANNEL.send(PacketHandler.CONTAINER_LISTENERS.with(() -> chunks), new UpdateHeatNetworkPacket(this));
        }
    }

    protected void refreshTransferPoint(BlockPos pos, @Nonnull Cable c) {
        boolean hasConnections = c.hasTransferConnections();
        TransferPoint n = nodes.get(pos);
        if(n == null) {
            if(!hasConnections) return;
            n = new TransferPoint(c);
            nodes.put(pos, n);
        } else {
            if (hasConnections) n.recheckConnections();
            else nodes.remove(pos).invalidate();
        }
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

    public Level getWorld() {
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
        return internalEnergy.doubleValue() / getHeatCapacity();
    }

    @Override
    public FixedPointNumber getInternalEnergy() {
        return internalEnergy;
    }

    @Override
    public void setTemperature(double temperature) {
        setInternalEnergy((long) (temperature * getHeatCapacity()));
    }

    @Override
    public void setInternalEnergy(long energy) {
        this.internalEnergy.set(energy);
        if(internalEnergy.isLessThan(0)) internalEnergy.set(0);
        onChanged();
    }

    @Override
    public void setInternalEnergy(FixedPointNumber energy) {
        this.internalEnergy.set(energy);
        if(internalEnergy.isLessThan(0)) internalEnergy.set(0);
        onChanged();
    }

    @Override
    public double getHeatCapacity() {
        return heatCapacity;
    }

    @Override
    public void setHeatCapacity(double capacity, boolean updateEnergy) {
        if(updateEnergy) {
            setInternalEnergy((long) (getInternalEnergy().doubleValue() + (capacity - getHeatCapacity()) * IHeatHandler.AIR_TEMPERATURE));
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
        this.internalEnergy.add(energy);
        onChanged();
    }

    @Override
    public void transferEnergy(FixedPointNumber energy) {
        this.internalEnergy.add(energy);
        onChanged();
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
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("internalEnergy", getInternalEnergy().longValue());
        nbt.putDouble("heatCapacity", getHeatCapacity());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setInternalEnergy(nbt.getLong("internalEnergy"));
        setHeatCapacity(nbt.getDouble("heatCapacity"), false);
    }

    public INetworkBinding getBinding() {
        return dataHolder.getBinding();
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
            for (Map.Entry<Direction, TransferType> entry : cable.getConnections().entrySet()) {
                Direction dir = entry.getKey();
                TransferType transferType = entry.getValue();
                if(!transferType.canTransfer()) continue;

                globalTransferType = globalTransferType.or(transferType);
                BlockEntity tile = world.getBlockEntity(cable.getPos().relative(dir));
                if (tile == null || tile instanceof HeatTransmitterTile) continue;

                LazyOptional<IHeatHandler> handler = tile.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, dir.getOpposite());
                if(handler.map(IHeatHandler::canReceive).orElse(false)) {
                    handler.addListener(lazy -> invalidateConnection(dir));
                    receivers.put(dir, handler);
                    receiverCount++;
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

    public class DataHolder implements IDataHolder {

        private final Map<ResourceLocation, DataReference<?>> references;

        private final INetworkBinding binding;

        public DataHolder() {
            this.references = new HashMap<>();
            binding = new CompositeDataNetworkBinding(this);
        }

        @Override
        public INetworkBinding getBinding() {
            return binding;
        }

        @Override
        public DataHolderCategory getCategory() {
            return DataHolderCategory.HEAT_NETWORK;
        }

        @Override
        public DataReference<?> getData(ResourceLocation id) {
            return references.get(id);
        }

        @Override
        public void forEach(BiConsumer<ResourceLocation, DataReference<?>> action) {
            references.forEach(action);
        }

        @Override
        public <T> void addData(DataReference<T> data) {
            references.put(data.getId(), data);
        }

        @Override
        public <T> void addData(DataType<T> type, ResourceLocation key, Supplier<T> getter, Consumer<T> setter) {
            addData(new DataReference<>(type, key, getter, v -> {
                setter.accept(v);
                onChanged();
            }));
        }
    }
}
