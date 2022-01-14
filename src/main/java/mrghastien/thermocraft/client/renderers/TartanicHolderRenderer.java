package mrghastien.thermocraft.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import mrghastien.thermocraft.common.blocks.machines.tartanicholder.TartanicHolderBlockEntity;
import mrghastien.thermocraft.common.registries.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;

public class TartanicHolderRenderer implements BlockEntityRenderer<TartanicHolderBlockEntity> {

    private final BlockEntityRendererProvider.Context ctx;
    private float angle = 0f;

    public TartanicHolderRenderer(BlockEntityRendererProvider.Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void render(@Nonnull TartanicHolderBlockEntity pBlockEntity, float pPartialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if(pBlockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(h -> !h.getStackInSlot(0).isEmpty()).orElse(false)) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        Quaternion rotation = new Quaternion(Vector3f.YP, angle, true);
        poseStack.mulPose(rotation);

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = new ItemStack(ModItems.TARTANE_CRYSTAL.get());
        BakedModel itemModel = renderer.getModel(stack, pBlockEntity.getLevel(), null, 0);
        renderer.render(stack, ItemTransforms.TransformType.FIXED, false, poseStack, pBufferSource, pPackedLight, pPackedOverlay, itemModel);

        poseStack.popPose();
        angle += 0.5f;
    }
}
