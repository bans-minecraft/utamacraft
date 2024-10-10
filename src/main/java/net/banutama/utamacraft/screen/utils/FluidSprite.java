package net.banutama.utamacraft.screen.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class FluidSprite {
    public static int getTint(FluidStack fluid) {
        return IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid);
    }

    public static void setColorFromTint(int tint) {
        float a = ((tint >> 24) & 0xff) / 255.0f;
        float r = ((tint >> 16) & 0xff) / 255.0f;
        float g = ((tint >> 8) & 0xff) / 255.0f;
        float b = (tint & 0xff) / 255.0f;
        RenderSystem.setShaderColor(r, g, b, a);
    }

    public static TextureAtlasSprite getStillFluidSprite(FluidStack fluid) {
        ResourceLocation texture = IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid);
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
    }
}
