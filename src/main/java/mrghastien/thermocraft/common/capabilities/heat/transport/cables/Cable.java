package mrghastien.thermocraft.common.capabilities.heat.transport.cables;

import mrghastien.thermocraft.api.heat.IHeatHandler;
import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.capabilities.Capabilities;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetwork;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.blocks.transmitters.HeatTransmitterBlockEntity;
import mrghastien.thermocraft.util.Constants;
import mrghastien.thermocraft.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Cable {

    protected HeatNetwork network;
    protected Level world;
    protected final EnumMap<Direction, TransferType> connections;
    private final EnumSet<Direction> cableConnections;
    private final EnumSet<Direction> otherConnections; //Cache
    protected final BlockPos pos;
    protected final Supplier<BlockState> stateSupplier;
    protected final HeatTransmitterBlockEntity<?> tileEntity;
    private boolean valid = true;

    public Cable(Level world, BlockPos pos, HeatTransmitterBlockEntity<?> tileEntity) {
        this.world = world;
        this.pos = pos;
        this.stateSupplier = () -> world.getBlockState(pos);
        this.connections = new EnumMap<>(Direction.class);
        this.cableConnections = EnumSet.noneOf(Direction.class);
        this.otherConnections = EnumSet.noneOf(Direction.class);
        this.tileEntity = tileEntity;
        if(!world.isClientSide()) {
            final HeatNetworkHandler instance = HeatNetworkHandler.instance();
            instance.registerUnassigned(pos, this);
        }
    }

    public HeatTransmitterBlockEntity<?> getTileEntity() {
        return tileEntity;
    }

    public EnumSet<Direction> getCableConnections() {
        return cableConnections;
    }

    public EnumSet<Direction> getOtherConnections() {
        return otherConnections;
    }

    public EnumMap<Direction, TransferType> getConnections() {
        return connections;
    }

    public boolean isPump() {
        return false;
    }

    public boolean isIntersection() {
        return cableConnections.size() > 2;
    }

    public HeatNetwork getNetwork() {
        return network;
    }

    public boolean hasNetwork() {
        return getNetwork() != null;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Level getWorld() {
        return network == null ? world : network.getWorld();
    }

    public abstract HeatNetworkHandler.HeatNetworkType getType();

    public void setNetwork(HeatNetwork net) {
        if(net != network) {
            network = net;
            ThermoCraft.LOGGER.debug("Changed network at " + pos + " with " + net);
        }
    }

    BlockState getBlockState() {
        return stateSupplier.get();
    }

    public boolean updateDirection(Direction dir) {
        BlockPos adj = pos.relative(dir);
        if(canConnect(dir)) {
            return changeConnection(dir, TransferType.NEUTRAL);
        } else {
            BlockEntity te = world.getBlockEntity(adj);
            if(te != null) {
                LazyOptional<IHeatHandler> handler = te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, dir.getOpposite());
                boolean canReceive = handler.map(IHeatHandler::canReceive).orElse(false);
                boolean canExtract = handler.map(IHeatHandler::canExtract).orElse(false);
                TransferType type = TransferType.get(canExtract, canReceive);
                return changeConnection(dir, type);
            } else return changeConnection(dir, TransferType.NONE);
        }
    }

    public boolean updateDirections() {
        boolean changed = false;
        for (Direction dir : Constants.DIRECTIONS) {
            changed |= updateDirection(dir);
        }
        if(changed) ThermoCraft.LOGGER.debug("Cable connections changed");
        return changed;
    }

    public boolean canConnect(Direction dir) {
        Cable adjacent = ModUtils.getCable(pos.relative(dir), world);
        return adjacent != null && adjacent.getType() == getType();
    }

    protected boolean changeConnection(Direction dir, TransferType type) {
        TransferType previous = connections.put(dir, type);
        if(previous != type) {
            if(previous == TransferType.NEUTRAL)  {
                getCableConnections().remove(dir);
            }

            if(type == TransferType.NEUTRAL) {
                getCableConnections().add(dir);
            } else if(type != TransferType.NONE){
                getOtherConnections().add(dir);
            }
            return true;
        }
        return false;
    }

    public boolean hasTransferConnections() {
        for(TransferType transfer : connections.values()) {
            if(transfer.canTransfer()) return true;
        }
        return false;
    }

    public void writeToNbt(CompoundTag nbt) {
        for(Map.Entry<Direction, TransferType> e : connections.entrySet()) {
            nbt.putString(e.getKey().getName(), e.getValue().getSerializedName());
        }
    }

    public boolean handleUpdateTag(CompoundTag nbt) {
        boolean result = false;
        for(Direction dir : Constants.DIRECTIONS) {
            result |= changeConnection(dir, TransferType.fromString(nbt.getString(dir.getName())));
        }
        return result;
    }

    public void onRemoved() {
        this.valid = false;
        if(world.isClientSide() || network == null) return;
        HeatNetworkHandler.instance().removeFromNetwork(network, this);
    }

    public boolean isValid() {
        return valid;
    }

    public void onChunkUnload() {
        if(network == null) return;
    }
}
