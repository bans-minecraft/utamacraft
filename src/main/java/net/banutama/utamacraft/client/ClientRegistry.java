package net.banutama.utamacraft.client;

import dan200.computercraft.api.client.ComputerCraftAPIClient;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import net.banutama.utamacraft.CCRegistration;
import net.banutama.utamacraft.Utamacraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Utamacraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistry {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.PLAYER_TURTLE.get(),
                TurtleUpgradeModeller.flatItem());
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.TELEPORTER_TURTLE.get(),
                TurtleUpgradeModeller.flatItem());
    }
}
