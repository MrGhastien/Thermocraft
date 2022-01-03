package mrghastien.thermocraft.common.blocks;

import mrghastien.thermocraft.common.network.data.BlockEntityDataHolder;
import mrghastien.thermocraft.common.network.data.DataReference;
import mrghastien.thermocraft.common.network.data.IDataHolder;
import mrghastien.thermocraft.common.network.data.ReadOnlyDataHolder;
import mrghastien.thermocraft.common.network.packets.ModUpdateBlockEntityPacket;
import mrghastien.thermocraft.common.network.packets.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Base class for all the tile entities of the ThermoCraft mod.
 */
public abstract class MachineBlockEntity extends BlockEntity implements MenuProvider {

    protected long tickCount = 0;

    @Nullable
    private final IDataHolder holder;

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean syncDirectly) {
        super(type, pos, state);
        if(syncDirectly)
            holder = new BlockEntityDataHolder(this);
        else holder = null;
    }

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, false);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(holder != null) registerSyncData(holder);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("NAME");
    }

    protected abstract void loadInternal(CompoundTag nbt);

    protected abstract void saveInternal(CompoundTag nbt);

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return null;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();

    }

    protected void clientTick() {}

    protected void serverTick() {}

    void broadcastChanges() {
        if(holder != null && holder.getBinding().hasChanged())
            PacketHandler.MAIN_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(this::getChunk), new ModUpdateBlockEntityPacket(worldPosition, holder.getBinding()));
    }

    protected void updateBlockState(BlockState newState) {
        if (level == null) return;
        BlockState oldState = level.getBlockState(worldPosition);
        if (oldState != newState) {
            level.setBlock(worldPosition, newState, 3);
            //level.notifyBlockUpdate(worldPosition, oldState, newState, 3);
        }
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        loadInternal(nbt.getCompound("Internal"));
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag nbt) {
        super.save(nbt);
        CompoundTag internal = new CompoundTag();
        nbt.put("Internal", internal);
        saveInternal(internal);
        return nbt;
    }

    public LevelChunk getChunk() {
        return level.getChunkAt(worldPosition);
    }

    /**
     * Used to register data references (mainly for client-server sync)
     * @param holder The {@link IDataHolder} holding data references
     *
     * @see DataReference
     */
    public void registerSyncData(IDataHolder holder) {}

    @Nullable
    public IDataHolder getDataHolder() {
        return new ReadOnlyDataHolder(holder);
    }
}
