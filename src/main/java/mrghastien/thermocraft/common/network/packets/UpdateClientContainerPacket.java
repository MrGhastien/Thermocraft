package mrghastien.thermocraft.common.network.packets;

import io.netty.buffer.Unpooled;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.inventory.menus.BaseContainer;
import mrghastien.thermocraft.common.network.INetworkBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

/** Packet responsible for carrying data from the server to the client, and tell the client-side container
 * there is an update
 */
public class UpdateClientContainerPacket {

    private final FriendlyByteBuf buf;

    public UpdateClientContainerPacket(INetworkBinding binding) {
        this.buf = new FriendlyByteBuf(Unpooled.buffer());
        binding.encode(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBytes(this.buf);
    }

    public UpdateClientContainerPacket(FriendlyByteBuf buf) {
        this.buf = new FriendlyByteBuf(buf.copy());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if(player == null) {
                ThermoCraft.LOGGER.warn("Could not synchronize menu value, as the player no longer exists");
                return;
            }
            AbstractContainerMenu menu = mc.player.containerMenu;
            if(!(menu instanceof BaseContainer)) {
                ThermoCraft.LOGGER.debug("Could not synchronize menu value, as it is invalid");
                return;
            }
            BaseContainer baseContainer = (BaseContainer) menu;
            baseContainer.getDataHolder().getBinding().decode(buf);

        });
        ctx.get().setPacketHandled(true);
    }

}
