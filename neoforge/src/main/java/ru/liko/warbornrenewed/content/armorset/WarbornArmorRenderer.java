package ru.liko.warbornrenewed.content.armorset;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import javax.annotation.Nullable;

public class WarbornArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    private final ArmorVisualSpec visuals;

    public WarbornArmorRenderer(ArmorVisualSpec visuals, ArmorBonesSpec bones) {
        super(new WarbornArmorModel(visuals));
        this.visuals = visuals;
        bones.apply(this);
    }

    @Override
    public ResourceLocation getTextureLocation(WarbornArmorItem animatable) {
        ItemStack stack = this.currentStack;
        if (stack != null) {
            // TODO: Check for variant using DataComponents in 1.21.1
            String variant = ""; // Placeholder
            if (!variant.isEmpty() && visuals.variants().containsKey(variant)) {
                return visuals.variants().get(variant);
            }
        }
        return super.getTextureLocation(animatable);
    }

    @Override
    public RenderType getRenderType(WarbornArmorItem animatable, ResourceLocation texture,
            @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
public void actuallyRender(
        com.mojang.blaze3d.vertex.PoseStack poseStack,
        WarbornArmorItem animatable,
        software.bernie.geckolib.cache.object.BakedGeoModel model,
        RenderType renderType,
        MultiBufferSource bufferSource,
        com.mojang.blaze3d.vertex.VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour) {

    ItemStack stack = this.currentStack;

    if (stack != null && animatable.isDyeable()) {
        int dyeColor = net.minecraft.world.item.component.DyedItemColor.getOrDefault(
                stack,
                net.minecraft.world.item.component.DyedItemColor.LEATHER_COLOR
        );

        // Minecraft colors are 0xRRGGBB
        float red = ((dyeColor >> 16) & 0xFF) / 255.0F;
        float green = ((dyeColor >> 8) & 0xFF) / 255.0F;
        float blue = (dyeColor & 0xFF) / 255.0F;

        // GeckoLib expects ARGB
        colour =
                (0xFF << 24) |
                ((int)(red * 255) << 16) |
                ((int)(green * 255) << 8) |
                (int)(blue * 255);
    }

    super.actuallyRender(
            poseStack,
            animatable,
            model,
            renderType,
            bufferSource,
            buffer,
            isReRender,
            partialTick,
            packedLight,
            packedOverlay,
            colour
    );
}
}
