package net.banutama.utamacraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class AmuletModel extends HumanoidModel<LivingEntity> {
    public AmuletModel(ModelPart part) {
        super(part, RenderType::entityTranslucent);
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(body);
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, @NotNull VertexConsumer pBuffer, int pPackedLight,
            int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        pPoseStack.pushPose();
        pPoseStack.scale(0.25f, 0.25f, 0.25f);
        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        pPoseStack.popPose();
    }

    public static MeshDefinition createFireWard() {
        CubeListBuilder body = CubeListBuilder.create();

        body.texOffs(0, 0);
        body.addBox(-7.0f, 2.5f, -12.5f, 14, 14, 1);

        MeshDefinition mesh = createMesh(CubeDeformation.NONE, 0);
        mesh.getRoot().addOrReplaceChild("body", body, PartPose.ZERO);

        return mesh;
    }
}
