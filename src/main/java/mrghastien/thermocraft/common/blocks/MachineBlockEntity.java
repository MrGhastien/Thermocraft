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
public abstract class MachineBlockEntity extends BaseBlockEntity implements MenuProvider {

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean syncDirectly) {
        super(type, pos, state, syncDirectly);
    }

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, false);
    }

    @Nonnull
    @Override
    public Component getDisplayName() {
        return new TextComponent("NAME");
    }

    protected void clientTick() {}

    protected void serverTick() {}
}
