package net.banutama.utamacraft;

import com.mojang.logging.LogUtils;

import net.banutama.utamacraft.block.custom.ModBlocks;
import net.banutama.utamacraft.block.entity.ModBlockEntities;
import net.banutama.utamacraft.integrations.curios.CuriosRenderers;
import net.banutama.utamacraft.integrations.curios.CuriousLayerDefinitions;
import net.banutama.utamacraft.item.ModCreativeModeTab;
import net.banutama.utamacraft.item.ModItems;

import net.banutama.utamacraft.networking.ModMessages;
import net.banutama.utamacraft.recipe.ModRecipes;
import net.banutama.utamacraft.screen.DigitizerScreen;
import net.banutama.utamacraft.screen.InsolatorScreen;
import net.banutama.utamacraft.screen.ModMenuTypes;
import net.banutama.utamacraft.sound.ModSounds;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Utamacraft.MOD_ID)
public class Utamacraft {
    public static final String MOD_ID = "utamacraft";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Utamacraft() {
        LOGGER.info("Utamacraft initializing");
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModeTab.register(bus);
        ModItems.register(bus);
        ModBlocks.register(bus);
        ModBlockEntities.register(bus);
        CCRegistration.register(bus);
        ModMenuTypes.register(bus);
        ModSounds.register(bus);
        ModRecipes.register(bus);

        bus.addListener(this::commonSetup);
        bus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModMessages.register();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.BULB);
            event.accept(ModItems.PCB);
            event.accept(ModItems.FIBER_GLASS);
            event.accept(ModItems.TUNGSTEN_INGOT);
            event.accept(ModItems.TUNGSTEN_RAW);
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.INSOLATOR_MENU.get(), InsolatorScreen::new);
            MenuScreens.register(ModMenuTypes.DIGITIZER_MENU.get(), DigitizerScreen::new);
            CuriosRenderers.register();
        }

//        @SubscribeEvent
//        public static void onIntermodEnqueue(InterModEnqueueEvent event) {
//            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
//                    () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
//        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModClientEvents {
        @SubscribeEvent
        public static void onRegisterRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
            event.registerRecipeCategoryFinder(ModRecipes.INSOLATOR_RECIPE_TYPE.get(), recipe -> {
                return RecipeBookCategories.UNKNOWN;
            });
        }

        @SubscribeEvent
        public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            CuriousLayerDefinitions.register(event);
        }
    }
}
