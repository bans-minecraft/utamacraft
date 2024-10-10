package net.banutama.utamacraft.screen.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import org.joml.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;

public class TiledSprite {
    public static void drawTiledSprite(PoseStack stack, int tiledWidth, int tiledHeight, int tint, int scaledAmount, TextureAtlasSprite sprite, int textureSize) {
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        Matrix4f m = stack.last().pose();
        FluidSprite.setColorFromTint(tint);

        int xTileCount = tiledWidth / textureSize;
        int xRemainder = tiledWidth - (xTileCount * textureSize);
        int yTileCount = scaledAmount / textureSize;
        int yRemainder = scaledAmount - (yTileCount * textureSize);

        for (int xTile = 0; xTile <= xTileCount; ++xTile) {
            for (int yTile = 0; yTile <= yTileCount; ++yTile) {
                int width = (xTile == xTileCount) ? xRemainder : textureSize;
                if (width <= 0) {
                    continue;
                }

                int height = (yTile == yTileCount) ? yRemainder : textureSize;
                if (height <= 0) {
                    continue;
                }

                int x = xTile * textureSize;
                int y = tiledHeight - ((yTile + 1) * textureSize);
                drawTextureWithMasking(m, x, y, sprite, textureSize - height, textureSize - width, 100);
            }
        }
    }

    private static void drawTextureWithMasking(Matrix4f m, float x, float y, TextureAtlasSprite sprite, int maskTop, int maskRight, float z) {
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        u1 = u1 - (maskRight / 16.0f * (u1 - u0));
        v1 = v1 - (maskTop / 16.0f * (v1 - v0));

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(m, x, y + 16, z).uv(u0, v1).endVertex();
        builder.vertex(m, x + 16 - maskRight, y + 16, z).uv(u1, v1).endVertex();
        builder.vertex(m, x + 16 - maskRight, y + maskTop, z).uv(u1, v0).endVertex();
        builder.vertex(m, x, y + maskTop, z).uv(u0, v0).endVertex();
        tesselator.end();
    }
}
