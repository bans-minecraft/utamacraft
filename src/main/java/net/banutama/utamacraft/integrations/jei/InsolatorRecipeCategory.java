package net.banutama.utamacraft.integrations.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.banutama.utamacraft.Utamacraft;
import net.banutama.utamacraft.block.custom.ModBlocks;
import net.banutama.utamacraft.recipe.InsolatorRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InsolatorRecipeCategory implements IRecipeCategory<InsolatorRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(Utamacraft.MOD_ID, "insolator");
    public final static ResourceLocation TEXTURE = new ResourceLocation(Utamacraft.MOD_ID,
            "textures/gui/insolator_gui.png");
    public static final RecipeType<InsolatorRecipe> INSOLATOR_TYPE = new RecipeType<>(InsolatorRecipeCategory.UID,
            InsolatorRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public InsolatorRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.INSOLATOR.get()));
    }

    @Override
    public @NotNull RecipeType<InsolatorRecipe> getRecipeType() {
        return INSOLATOR_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("block.utamacraft.insolator");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull InsolatorRecipe recipe,
            @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 152, 10)
                .addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 78, 10)
                .addIngredients(ForgeTypes.FLUID_STACK, List.of(recipe.getFluid()))
                .setFluidRenderer(64000, false, 9, 60);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 152, 10)
                .addItemStack(recipe.getResultItem(null));
    }
}
