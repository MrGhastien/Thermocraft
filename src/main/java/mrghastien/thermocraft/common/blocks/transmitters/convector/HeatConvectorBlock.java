package mrghastien.thermocraft.common.blocks.transmitters.convector;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.blocks.transmitters.HeatTransmitterBlock;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.blocks.transmitters.HeatTransmitterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeatConvectorBlock extends HeatTransmitterBlock {

    public HeatConvectorBlock() {
        super(Properties.of(Material.METAL).noOcclusion().strength(5f));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        if(!world.isClientSide()) return InteractionResult.PASS;
        if(player.getItemInHand(hand).getItem() == Items.BOWL) {
            BlockEntity te = world.getBlockEntity(pos);
            if(te instanceof HeatTransmitterBlockEntity<?>)
                player.sendMessage(new TextComponent("Cable connections : " + ((HeatTransmitterBlockEntity<?>)te).getCable().getCableConnections()), player.getUUID());
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        ThermoCraft.LOGGER.debug("SetPlacedBy");
        if(itemStack.getItem() == asItem()) {
            CompoundTag nbt = itemStack.getTag();
            Fluid fluid;
            if(nbt != null && nbt.contains("fluid")) {
                ResourceLocation fluidLocation = new ResourceLocation(nbt.getString("fluid"));
                fluid = ForgeRegistries.FLUIDS.getValue(fluidLocation);
            } else fluid = Fluids.EMPTY;

            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof HeatConvectorBlockEntity) {
                ((HeatConvectorBlockEntity) te).setFluid(fluid);
            }
        }
    }

    @Override
    public HeatNetworkHandler.HeatNetworkType getNetworkType() {
        return HeatNetworkHandler.HeatNetworkType.CONVECTOR;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
        return new HeatConvectorBlockEntity(blockPos, blockState);
    }
}
