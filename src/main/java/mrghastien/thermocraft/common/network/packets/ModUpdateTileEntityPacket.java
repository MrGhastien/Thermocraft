package mrghastien.thermocraft.common.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class ModUpdateTileEntityPacket {

    private final BlockPos pos;
    private final CompoundTag nbt;

    public ModUpdateTileEntityPacket(BlockPos pos, CompoundTag nbt) {
        this.pos = pos;
        this.nbt = nbt;
    }

    ModUpdateTileEntityPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.nbt = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeNbt(nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world = Minecraft.getInstance().level;
            if(world.hasChunkAt(pos)) {
                BlockEntity te = world.getBlockEntity(pos);
                if (te != null) te.handleUpdateTag(nbt);
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
