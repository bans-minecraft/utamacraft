package net.banutama.utamacraft.integrations.curios;

import net.banutama.utamacraft.Utamacraft;
import net.banutama.utamacraft.client.model.AmuletModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class CuriousLayerDefinitions {
    public static final ModelLayerLocation FIRE_WARD = new ModelLayerLocation(
            new ResourceLocation(Utamacraft.MOD_ID, "fire_ward"), "fire_ward");

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FIRE_WARD, () -> {
            return LayerDefinition.create(AmuletModel.createFireWard(), 16, 16);
        });
    }
}
