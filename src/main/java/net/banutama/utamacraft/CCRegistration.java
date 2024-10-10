package net.banutama.utamacraft;

import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import net.banutama.utamacraft.block.entity.AwarenessBlockEntity;
import net.banutama.utamacraft.block.entity.DigitizerBlockEntity;
import net.banutama.utamacraft.block.entity.InsolatorBlockEntity;
import net.banutama.utamacraft.integrations.computercraft.PeripheralProvider;
import net.banutama.utamacraft.integrations.computercraft.peripheral.AwarenessBlockPeripheral;
import net.banutama.utamacraft.integrations.computercraft.peripheral.DigitizerPeripheral;
import net.banutama.utamacraft.integrations.computercraft.peripheral.InsolatorPeripheral;
import net.banutama.utamacraft.integrations.computercraft.turtles.TurtlePlayerUpgrade;
import net.banutama.utamacraft.integrations.computercraft.turtles.TurtleTeleporterUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * CC:Tweaked registration
 */
public class CCRegistration {
    public static final DeferredRegister<TurtleUpgradeSerialiser<?>> TURTLE_SERIALIZERS = DeferredRegister
            .create(TurtleUpgradeSerialiser.registryId(), Utamacraft.MOD_ID);

    public static final RegistryObject<TurtleUpgradeSerialiser<TurtlePlayerUpgrade>> PLAYER_TURTLE = TURTLE_SERIALIZERS
            .register(ID.PLAYER_TURTLE.getPath(),
                    () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtlePlayerUpgrade::new));

    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleTeleporterUpgrade>> TELEPORTER_TURTLE = TURTLE_SERIALIZERS
            .register(ID.TELEPORTER_TURTLE.getPath(),
                    () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleTeleporterUpgrade::new));

    public static PeripheralProvider peripheralProvider = new PeripheralProvider();

    public static void register(IEventBus bus) {
        TURTLE_SERIALIZERS.register(bus);

        peripheralProvider.registerBlockPeripheral(InsolatorPeripheral::new, InsolatorBlockEntity.class::isInstance);
        peripheralProvider.registerBlockPeripheral(AwarenessBlockPeripheral::new,
                AwarenessBlockEntity.class::isInstance);
        peripheralProvider.registerBlockPeripheral(DigitizerPeripheral::new, DigitizerBlockEntity.class::isInstance);

        ForgeComputerCraftAPI.registerPeripheralProvider(peripheralProvider);
    }

    public static class ID {
        public static final ResourceLocation PLAYER_TURTLE = new ResourceLocation(Utamacraft.MOD_ID, "player_turtle");
        public static final ResourceLocation TELEPORTER_TURTLE = new ResourceLocation(Utamacraft.MOD_ID,
                "teleporter_turtle");
    }
}
