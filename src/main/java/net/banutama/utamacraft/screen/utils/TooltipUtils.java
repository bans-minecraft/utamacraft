package net.banutama.utamacraft.screen.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TooltipUtils {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();

    private static @NotNull List<Component> startFluidTooltip(@NotNull FluidStack stack) {
        List<Component> tooltip = new ArrayList<>();

        Fluid fluid = stack.getFluid();
        if (fluid.isSame(Fluids.EMPTY)) {
            tooltip.add(Component.translatable("tooltip.utamacraft.fluid.empty")
                    .withStyle(ChatFormatting.ITALIC)
                    .withStyle(ChatFormatting.GRAY));
            return tooltip;
        }

        tooltip.add(stack.getDisplayName());
        return tooltip;
    }

    public static @NotNull List<Component> getFluidTooltip(@NotNull FluidStack stack, int capacity) {
        List<Component> tooltip = startFluidTooltip(stack);

        int amount = stack.getAmount();
        int mb = (amount * 1000) / FluidType.BUCKET_VOLUME;

        tooltip.add(Component.translatable("tooltip.utamacraft.fluid.amount.with_capacity",
                        nf.format(mb), nf.format(capacity))
                .withStyle(ChatFormatting.GRAY));

        return tooltip;
    }

    public static @NotNull List<Component> getFluidTooltip(@NotNull FluidStack stack) {
        List<Component> tooltip = startFluidTooltip(stack);

        int amount = stack.getAmount();
        int mb = (amount * 1000) / FluidType.BUCKET_VOLUME;

        tooltip.add(Component.translatable("tooltip.utamacraft.fluid.amount", nf.format(mb))
                .withStyle(ChatFormatting.GRAY));

        return tooltip;
    }

    public static @NotNull List<Component> getEnergyTooltip(int energy) {
        return List.of(Component.translatable("tooltip.utamacraft.energy.amount",
                nf.format(energy)).withStyle(ChatFormatting.GRAY));
    }

    public static @NotNull List<Component> getEnergyTooltip(int energy, int capacity) {
        return List.of(
                Component.translatable(("tooltip.utamacraft.energy")),
                Component.translatable("tooltip.utamacraft.energy.amount.with_capacity",
                    nf.format(energy), nf.format(capacity)).withStyle(ChatFormatting.GRAY));
    }
}
