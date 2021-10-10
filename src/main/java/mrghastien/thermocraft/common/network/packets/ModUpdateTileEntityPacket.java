package mrghastien.thermocraft.common.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ModUpdateTileEntityPacket {

    private final BlockPos pos;
    private final CompoundNBT nbt;

    public ModUpdateTileEntityPacket(BlockPos pos, CompoundNBT nbt) {
        this.pos = pos;
        this.nbt = nbt;
    }

    ModUpdateTileEntityPacket(PacketBuffer buf) {
        this.pos = buf.readBlockPos();
        this.nbt = buf.readNbt();
    }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeNbt(nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            World world = Minecraft.getInstance().level;
            if(world.hasChunkAt(pos)) {
                TileEntity te = world.getBlockEntity(pos);
                if (te != null) te.handleUpdateTag(te.getBlockState(), nbt);
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
