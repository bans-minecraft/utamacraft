package net.banutama.utamacraft.item;

import net.banutama.utamacraft.Utamacraft;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            Utamacraft.MOD_ID);

    public static final RegistryObject<Item> PLAYER_PERIPHERAL = ITEMS.register("player_peripheral",
            PlayerPeripheralItem::new);
    public static final RegistryObject<Item> TELEPORTER_PERIPHERAL = ITEMS.register("teleporter_peripheral",
            TeleporterPeripheralItem::new);
    public static final RegistryObject<Item> TUNGSTEN_INGOT = ITEMS.register("tungsten_ingot", ModItems::simpleItem);
    public static final RegistryObject<Item> TUNGSTEN_RAW = ITEMS.register("tungsten_raw", ModItems::simpleItem);
    public static final RegistryObject<Item> FIBER_GLASS = ITEMS.register("fiber_glass", ModItems::simpleItem);
    public static final RegistryObject<Item> PCB = ITEMS.register("pcb", ModItems::simpleItem);
    public static final RegistryObject<Item> BULB = ITEMS.register("bulb", ModItems::simpleItem);
    public static final RegistryObject<FireWardItem> FIRE_WARD = ITEMS.register("fire_ward", FireWardItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static Item simpleItem() {
        return new Item(new Item.Properties());
    }
}
