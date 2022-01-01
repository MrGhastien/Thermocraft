package mrghastien.thermocraft.common.tileentities.cables;

import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.common.capabilities.Capabilities;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Cable;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetwork;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public abstract class HeatTransmitterTile<C extends Cable> extends BlockEntity {

    private static final ModelProperty<TransferType> NORTH_PROPERTY = new ModelProperty<>();
    private static final ModelProperty<TransferType> SOUTH_PROPERTY = new ModelProperty<>();
    private static final ModelProperty<TransferType> EAST_PROPERTY = new ModelProperty<>();
    private static final ModelProperty<TransferType> WEST_PROPERTY = new ModelProperty<>();
    private static final ModelProperty<TransferType> UP_PROPERTY = new ModelProperty<>();
    private static final ModelProperty<TransferType> DOWN_PROPERTY = new ModelProperty<>();

    public static final EnumMap<Direction, ModelProperty<TransferType>> PROPERTY_MAP = Util.make(new EnumMap<>(Direction.class), m -> {
       m.put(Direction.NORTH, NORTH_PROPERTY);
       m.put(Direction.SOUTH, SOUTH_PROPERTY);
       m.put(Direction.EAST, EAST_PROPERTY);
       m.put(Direction.WEST, WEST_PROPERTY);
       m.put(Direction.UP, UP_PROPERTY);
       m.put(Direction.DOWN, DOWN_PROPERTY);
    });

    C cable;

    public HeatTransmitterTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }



    @Override
    public void onLoad() {
        super.onLoad();
        if(cable == null)
            this.cable = createCable();
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        if(cable.updateDirections() && cable.hasNetwork()) {
            cable.getNetwork().requestRefresh(worldPosition, cable);
        }
        CompoundTag tag = super.getUpdateTag();
        cable.writeToNbt(tag);
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(getBlockPos(), -1, getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if(cable.handleUpdateTag(tag))
            requestModelDataUpdate();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        cable.onChunkUnload();
        cable = null;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        cable.onRemoved();
        cable = null;
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        ModelDataMap.Builder builder = new ModelDataMap.Builder();
        for(Map.Entry<Direction, ModelProperty<TransferType>> entry : PROPERTY_MAP.entrySet()) {
            Direction dir = entry.getKey();
            ModelProperty<TransferType> property = entry.getValue();
            TransferType transferType = cable.getConnections().get(dir);
            builder.withInitial(property, transferType);
        }
        return builder.build();
    }

    protected abstract C createCable();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.HEAT_HANDLER_CAPABILITY) {
            HeatNetwork net = getNetwork();
            if(net != null)
                return net.getLazy().cast();
        }
        return super.getCapability(cap, side);
    }

    public long getNetworkId() {
        HeatNetwork net = getNetwork();
        return net == null ? -1 : net.getId();
    }

    public HeatNetwork getNetwork() {
        return cable.getNetwork();
    }

    public C getCable() {
        return cable;
    }

    public boolean hasNetwork() {
        return getNetwork() != null;
    }

    public void OnNeighborChanged(Direction dir) {
        BlockPos neighbor = getBlockPos().relative(dir);
        if(hasLevel()) getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.DEFAULT);
    }
}
