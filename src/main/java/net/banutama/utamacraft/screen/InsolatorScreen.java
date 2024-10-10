package net.banutama.utamacraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.banutama.utamacraft.Utamacraft;
import net.banutama.utamacraft.networking.ModMessages;
import net.banutama.utamacraft.networking.packet.DumpInsolatorFluidPacket;
import net.banutama.utamacraft.screen.utils.FluidSprite;
import net.banutama.utamacraft.screen.utils.MouseUtils;
import net.banutama.utamacraft.screen.utils.TiledSprite;
import net.banutama.utamacraft.screen.utils.TooltipUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class InsolatorScreen extends AbstractContainerScreen<InsolatorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Utamacraft.MOD_ID,
            "textures/gui/insolator_gui.png");
    private static final Component DUMP_BUTTON = Component.translatable("gui.utamacraft.insolator.dump");
    private static final Component DUMP_BUTTON_TOOLTIP = Component
            .translatable("gui.utamacraft.insolator.dump.tooltip");
    private static final Component DUMP_BUTTON_TOOLTIP_EMPTY = Component
            .translatable("gui.utamacraft.insolator.dump.tooltip.empty");

    private int leftPos, topPos;
    private Button dumpButton;

    public InsolatorScreen(InsolatorMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        inventoryLabelY += 5;
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;

        dumpButton = addRenderableWidget(
                new Button.Builder(DUMP_BUTTON, this::onDumpPress)
                        .pos(leftPos + 8, topPos + 52)
                        .size(60, 20)
                        .build());
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderBulb(graphics);
        renderProgressArrow(graphics);
        renderFluid(graphics, leftPos + 99, topPos + 11);
        renderEnergy(graphics, leftPos + 112, topPos + 11);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (MouseUtils.isMouseOver(pMouseX, pMouseY, x + 98, y + 10, 11, 62)) {
            FluidTank tank = menu.getBlockEntity().getFluidTank();
            List<Component> components = TooltipUtils.getFluidTooltip(tank.getFluid(), 64000);
            graphics.renderTooltip(font, components, Optional.empty(), pMouseX - x, pMouseY - y);
        }

        if (MouseUtils.isMouseOver(pMouseX, pMouseY, x + 111, y + 10, 11, 62)) {
            EnergyStorage energy = menu.getBlockEntity().getEnergy();
            List<Component> components = TooltipUtils.getEnergyTooltip(energy.getEnergyStored(),
                    energy.getMaxEnergyStored());
            graphics.renderTooltip(font, components, Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    private void renderEnergy(@NotNull GuiGraphics graphics, int x, int y) {
        EnergyStorage energy = menu.getBlockEntity().getEnergy();
        if (energy.getEnergyStored() <= 0 || energy.getMaxEnergyStored() <= 0) {
            return;
        }

        final int ENERGY_HEIGHT = 60;
        int stored = (int) (ENERGY_HEIGHT * ((float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored()));
        graphics.fillGradient(x, y + (ENERGY_HEIGHT - stored), x + 9, y + 60, 0xffb51500, 0xff600b00);
    }

    private void renderFluid(@NotNull GuiGraphics graphics, int x, int y) {
        FluidStack fluid = menu.getBlockEntity().getFluidTank().getFluid();
        if (fluid.getFluid().isSame(Fluids.EMPTY)) {
            return;
        }

        final int FLUID_HEIGHT = 60;

        TextureAtlasSprite sprite = FluidSprite.getStillFluidSprite(fluid);
        int tint = FluidSprite.getTint(fluid);
        int amount = fluid.getAmount();
        int scaled = Math.min(FLUID_HEIGHT, Math.max(amount > 0 ? 1 : 0, (amount * FLUID_HEIGHT) / 64000));

        RenderSystem.enableBlend();
        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0);

        TiledSprite.drawTiledSprite(pose, 9, FLUID_HEIGHT, tint, scaled, sprite, 16);

        pose.popPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    private void renderProgressArrow(GuiGraphics graphics) {
        if (menu.getBlockEntity().getActive()) {
            int ticks = menu.getBlockEntity().getTicks();
            if (ticks != 0) {
                float ratio = (float) menu.getBlockEntity().getProgress() / (float) ticks;
                graphics.blit(TEXTURE, leftPos + 156, topPos + 28, 176, 0, 8, (int)(25.0f * ratio));
            }
        }
    }

    private void renderBulb(GuiGraphics graphics) {
        if (menu.getBlockEntity().getActive()) {
            graphics.blit(TEXTURE, leftPos + 133, topPos + 37, 176, 25, 9, 14);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void onDumpPress(Button button) {
        ModMessages.sendToServer(new DumpInsolatorFluidPacket(menu.getBlockEntity().getBlockPos()));
    }
}
