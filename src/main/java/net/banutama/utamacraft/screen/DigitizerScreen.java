package net.banutama.utamacraft.screen;

import java.util.List;
import java.util.Optional;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.banutama.utamacraft.Utamacraft;
import net.banutama.utamacraft.screen.utils.MouseUtils;
import net.banutama.utamacraft.screen.utils.TooltipUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.energy.EnergyStorage;

public class DigitizerScreen extends AbstractContainerScreen<DigitizerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Utamacraft.MOD_ID,
            "textures/gui/digitizer_gui.png");

    public DigitizerScreen(DigitizerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        inventoryLabelY += 5;
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics gui, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderEnergy(gui, leftPos + 8, topPos + 8);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics gui, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (MouseUtils.isMouseOver(pMouseX, pMouseY, x + 7, y + 7, 11, 62)) {
            EnergyStorage energy = menu.getBlockEntity().getEnergy();
            List<Component> components = TooltipUtils.getEnergyTooltip(energy.getEnergyStored(),
                    energy.getMaxEnergyStored());
            gui.renderTooltip(font, components, Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    private void renderEnergy(@NotNull GuiGraphics gui, int x, int y) {
        EnergyStorage energy = menu.getBlockEntity().getEnergy();
        if (energy.getEnergyStored() <= 0 || energy.getMaxEnergyStored() <= 0) {
            return;
        }

        final int ENERGY_HEIGHT = 60;
        int stored = (int) (ENERGY_HEIGHT * ((float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored()));
        gui.fillGradient(x, y + (ENERGY_HEIGHT - stored), x + 9, y + 60, 0xffb51500, 0xff600b00);
    }

    @Override
    public void render(@NotNull GuiGraphics gui, int mouseX, int mouseY, float delta) {
        renderBackground(gui);
        super.render(gui, mouseX, mouseY, delta);
        renderTooltip(gui, mouseX, mouseY);
    }
}
