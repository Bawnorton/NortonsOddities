package com.bawnorton.tempadwithshaders.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.codexadrian.tempad.client.render.TimedoorRenderer;
import net.irisshaders.iris.Iris;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TimedoorRenderer.class)
public abstract class TimedoorRendererMixin {
    @WrapOperation(
            method = "render(Lme/codexadrian/tempad/common/entity/TimedoorEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/codexadrian/tempad/client/render/TimedoorRenderer;renderTimedoor(Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;FFFII)V"
            ),
            remap = false
    )
    private void useAlternateRenderingWhenShaders(TimedoorRenderer instance, Matrix4f model, MultiBufferSource multiBufferSource, float width, float height, float depth, int i, int color, Operation<Void> original, @Local(argsOnly = true) PoseStack poseStack) {
        Iris.getCurrentPack().ifPresentOrElse(s -> tempadWithShaders$alternateRendering(poseStack, multiBufferSource, width, height, depth, i), () -> original.call(instance, model, multiBufferSource, width, height, depth, i, color));
    }

    @Unique
    private void tempadWithShaders$alternateRendering(PoseStack poseStack, MultiBufferSource multiBufferSource, float width, float height, float depth, int i) {
        float xBound = width / 2.0F;
        float yBound = height / 2.0F + 0.01f;
        float zBound = depth / 2.0F;

        ResourceLocation texture = new ResourceLocation("tempadwithshaders", "base.png");
        Matrix4f model = poseStack.last().pose();
        VertexConsumer buffer = new SheetedDecalTextureGenerator(multiBufferSource.getBuffer(RenderType.entityTranslucent(texture)), model, poseStack.last().normal(), 1);
        buffer.vertex(model, -xBound, yBound, -zBound).uv(0.0F, 0.0F).uv2(i).endVertex();
        buffer.vertex(model, -xBound, -yBound, -zBound).uv(0.0F, 1.0F).uv2(i).endVertex();
        buffer.vertex(model, xBound, -yBound, -zBound).uv(1.0F, 1.0F).uv2(i).endVertex();
        buffer.vertex(model, xBound, yBound, -zBound).uv(1.0F, 0.0F).uv2(i).endVertex();
        buffer.vertex(model, xBound, yBound, zBound).uv(0.0F, 0.0F).uv2(i).endVertex();
        buffer.vertex(model, xBound, -yBound, zBound).uv(0.0F, 1.0F).uv2(i).endVertex();
        buffer.vertex(model, -xBound, -yBound, zBound).uv(1.0F, 1.0F).uv2(i).endVertex();
        buffer.vertex(model, -xBound, yBound, zBound).uv(1.0F, 0.0F).uv2(i).endVertex();
        buffer.vertex(model, -xBound, yBound, zBound).uv(0.0F, 0.0F).uv2(i).endVertex();
        buffer.vertex(model, -xBound, yBound, -zBound).uv(0.0F, 1.0F).uv2(i).endVertex();
        buffer.vertex(model, xBound, yBound, -zBound).uv(1.0F, 1.0F).uv2(i).endVertex();
        buffer.vertex(model, xBound, yBound, zBound).uv(1.0F, 0.0F).uv2(i).endVertex();
        buffer.vertex(model, -xBound, -yBound, -zBound).uv(0.0F, 0.0F).uv2(i).endVertex();
        buffer.vertex(model, -xBound, -yBound, zBound).uv(0.0F, 1.0F).uv2(i).endVertex();
        buffer.vertex(model, xBound, -yBound, zBound).uv(1.0F, 1.0F).uv2(i).endVertex();
        buffer.vertex(model, xBound, -yBound, -zBound).uv(1.0F, 0.0F).uv2(i).endVertex();
        buffer.vertex(model, -xBound, yBound, zBound).uv(0.0F, 0.0F).uv2(i).endVertex();
        buffer.vertex(model, -xBound, -yBound, zBound).uv(0.0F, 1.0F).uv2(i).endVertex();
        buffer.vertex(model, -xBound, -yBound, -zBound).uv(1.0F, 1.0F).uv2(i).endVertex();
        buffer.vertex(model, -xBound, yBound, -zBound).uv(1.0F, 0.0F).uv2(i).endVertex();
        buffer.vertex(model, xBound, yBound, -zBound).uv(0.0F, 0.0F).uv2(i).endVertex();
        buffer.vertex(model, xBound, -yBound, -zBound).uv(0.0F, 1.0F).uv2(i).endVertex();
        buffer.vertex(model, xBound, -yBound, zBound).uv(1.0F, 1.0F).uv2(i).endVertex();
        buffer.vertex(model, xBound, yBound, zBound).uv(1.0F, 0.0F).uv2(i).endVertex();
    }
}
