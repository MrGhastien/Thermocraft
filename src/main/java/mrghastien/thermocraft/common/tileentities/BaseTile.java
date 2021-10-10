package mrghastien.thermocraft.common.tileentities;

import mrghastien.thermocraft.common.inventory.containers.IThermocraftContainerProvider;
import mrghastien.thermocraft.common.network.NetworkDataType;
import mrghastien.thermocraft.common.network.NetworkHandler;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Base class for all the tile entities of the Thermo Craft mod.
 */
public abstract class BaseTile extends TileEntity implements IThermocraftContainerProvider, ITickableTileEntity {

    protected long tickCount;

    public BaseTile(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        NetworkHandler.getInstance(level).remove(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        registerTEUpdatedInfo();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("NAME");
    }

    protected abstract void loadInternal(BlockState state, CompoundNBT nbt);

    protected abstract void saveInternal(CompoundNBT nbt);

    @Override
    public void tick() {
        if(level.isClientSide) return;
        tickCount++;
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
    public void setRemoved() {
        super.setRemoved();
        NetworkHandler.getInstance(level).remove(this);
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

    public long getTickCount() {
        return tickCount;
    }

    public Chunk getChunk() {
        return level.getChunkAt(worldPosition);
    }

    public void registerTEUpdatedInfo() {
        NetworkHandler.getInstance(level).add(NetworkDataType.LONG, PacketDistributor.TRACKING_CHUNK.with(this::getChunk), this, this::getTickCount, v -> tickCount = (long) v);
    }
}
