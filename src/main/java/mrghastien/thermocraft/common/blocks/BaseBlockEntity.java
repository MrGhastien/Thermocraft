package mrghastien.thermocraft.common.blocks;

import mrghastien.thermocraft.common.network.data.BlockEntityDataHolder;
import mrghastien.thermocraft.common.network.data.DataReference;
import mrghastien.thermocraft.common.network.data.IDataHolder;
import mrghastien.thermocraft.common.network.data.ReadOnlyDataHolder;
import mrghastien.thermocraft.common.network.packets.ModUpdateBlockEntityPacket;
import mrghastien.thermocraft.common.network.packets.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;

public class BaseBlockEntity extends BlockEntity {

    @Nullable
    private final IDataHolder holder;

    public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean syncDirectly) {
        super(type, pos, state);
        if(syncDirectly)
            holder = new BlockEntityDataHolder(this);
        else holder = null;
    }

    public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, false);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(holder != null) registerSyncData(holder);
    }

    void broadcastChanges() {
        if(holder != null && holder.hasChanged())
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
