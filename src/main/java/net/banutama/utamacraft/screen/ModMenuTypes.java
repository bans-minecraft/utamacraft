package net.banutama.utamacraft.screen;

import net.banutama.utamacraft.Utamacraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
            Utamacraft.MOD_ID);

    public static final RegistryObject<MenuType<InsolatorMenu>> INSOLATOR_MENU = registerMenuType("insolator_menu",
            InsolatorMenu::new);
    public static final RegistryObject<MenuType<DigitizerMenu>> DIGITIZER_MENU = registerMenuType("digitizer_menu",
            DigitizerMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name,
            IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
