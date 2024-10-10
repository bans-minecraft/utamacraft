package net.banutama.utamacraft.integrations.curios;

import net.banutama.utamacraft.client.model.AmuletModel;
import net.banutama.utamacraft.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosRenderers {
    public static void register() {
        CuriosRendererRegistry.register(ModItems.FIRE_WARD.get(), () -> {
            return new CuriosRenderer("fire_ward", new AmuletModel(bakeLayer(CuriousLayerDefinitions.FIRE_WARD)));
        });
    }

    private static ModelPart bakeLayer(ModelLayerLocation layerLocation) {
        return Minecraft.getInstance().getEntityModels().bakeLayer(layerLocation);
    }
}
