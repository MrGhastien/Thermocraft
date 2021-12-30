package mrghastien.thermocraft.common.tileentities;

import mrghastien.thermocraft.common.network.data.IDataHolder;
import mrghastien.thermocraft.common.network.data.TileEntityDataHolder;
import mrghastien.thermocraft.common.network.data.DataReference;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;

/**
 * Base class for all the tile entities of the ThermoCraft mod.
 */
public abstract class BaseTile extends TileEntity implements INamedContainerProvider, ITickableTileEntity {

    @Nullable
    private final IDataHolder holder;

    public BaseTile(TileEntityType<?> type, boolean syncDirectly) {
        super(type);
        if(syncDirectly)
            holder = new TileEntityDataHolder(this);
        else holder = null;
    }

    public BaseTile(TileEntityType<?> type) {
        this(type, false);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(holder != null) registerSyncData(holder);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("NAME");
    }

    protected abstract void loadInternal(BlockState state, CompoundNBT nbt);

    protected abstract void saveInternal(CompoundNBT nbt);

    @Override
    public final void tick() {
        if(level.isClientSide)
            clientTick();
        else
            serverTick();
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return null;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getTag();

    }

    protected void clientTick() {}

    protected void serverTick() {}

    protected void updateBlockState(BlockState newState) {
        if (level == null) return;
        BlockState oldState = level.getBlockState(worldPosition);
        if (oldState != newState) {
            level.setBlock(worldPosition, newState, 3);
            //level.notifyBlockUpdate(worldPosition, oldState, newState, 3);
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        loadInternal(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        saveInternal(nbt);
        return nbt;
    }

    public Chunk getChunk() {
        return level.getChunkAt(worldPosition);
    }

    /**
     * Used to register data references (mainly for client-server sync)
     * @param holder The {@link IDataHolder} holding data references
     *
     * @see DataReference
     */
    public void registerSyncData(IDataHolder holder) {}
}
