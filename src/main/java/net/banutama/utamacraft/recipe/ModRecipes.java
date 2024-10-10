package net.banutama.utamacraft.recipe;

import net.banutama.utamacraft.Utamacraft;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Utamacraft.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Utamacraft.MOD_ID);

    public static final RegistryObject<RecipeSerializer<InsolatorRecipe>> INSOLATOR_SERIALIZER =
            SERIALIZERS.register("insolator", () -> InsolatorRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<InsolatorRecipe>> INSOLATOR_RECIPE_TYPE =
            TYPES.register("insolator_recipe", () -> new RecipeType<InsolatorRecipe>() {
                @Override
                public String toString() {
                    return "insolator_recipe";
                }
            });

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
