package net.banutama.utamacraft.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.banutama.utamacraft.Utamacraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InsolatorRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final Ingredient input;
    private final FluidStack fluid;
    private final int ticks;

    public InsolatorRecipe(ResourceLocation id, ItemStack output, Ingredient input, FluidStack fluid, int ticks) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.fluid = fluid;
        this.ticks = ticks;
    }

    @Override
    public boolean matches(@NotNull SimpleContainer pContainer, @NotNull Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }

        return input.test(pContainer.getItem(1));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer pContainer, RegistryAccess access) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public int getTicks() {
        return ticks;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess access) {
        return output.copy();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.INSOLATOR_RECIPE_TYPE.get();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }


    //    public static class Type implements RecipeType<InsolatorRecipe> {
//        public static final Type INSTANCE = new Type();
//        public static final String ID = "insolator";
//        private Type() {
//        }
//    }

    public static class Serializer implements RecipeSerializer<InsolatorRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(Utamacraft.MOD_ID, "insolator");

        @Override
        public @NotNull InsolatorRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            Ingredient input = Ingredient.fromJson(pSerializedRecipe.getAsJsonObject("input"));
            FluidStack fluid = FluidStack.CODEC.decode(JsonOps.INSTANCE, pSerializedRecipe.get("fluid")).result().orElseThrow().getFirst();
            int ticks = pSerializedRecipe.get("ticks").getAsInt();
            return new InsolatorRecipe(pRecipeId, output, input, fluid, ticks);
        }

        @Override
        public @Nullable InsolatorRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, @NotNull FriendlyByteBuf pBuffer) {
            ItemStack output = pBuffer.readItem();
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            FluidStack fluid = pBuffer.readFluidStack();
            int ticks = pBuffer.readInt();
            return new InsolatorRecipe(pRecipeId, output, input, fluid, ticks);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf pBuffer, @NotNull InsolatorRecipe pRecipe) {
            pBuffer.writeItemStack(pRecipe.output, false);
            pRecipe.input.toNetwork(pBuffer);
            pBuffer.writeFluidStack(pRecipe.fluid);
            pBuffer.writeInt(pRecipe.ticks);
        }
    }
}
