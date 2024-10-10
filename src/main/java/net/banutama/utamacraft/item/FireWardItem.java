package net.banutama.utamacraft.item;

import net.banutama.utamacraft.Utamacraft;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nonnull;
import java.util.List;

public class FireWardItem extends Item implements ICurioItem {
    private static final ResourceLocation FIRE_WARD_TEXTURE =
            new ResourceLocation(Utamacraft.MOD_ID, "textures/item/fire_ward.png");

    private Object model;

    public FireWardItem() {
        super(getProperties());
    }

    @Override
    public boolean isFoil(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().isOnFire()) {
            slotContext.entity().clearFire();
        }
    }

    private static Properties getProperties() {
        return new Properties().stacksTo(1);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("tooltip.utamacraft.fire_ward")
                .withStyle(ChatFormatting.DARK_RED)
                .withStyle(ChatFormatting.BOLD));
    }
}