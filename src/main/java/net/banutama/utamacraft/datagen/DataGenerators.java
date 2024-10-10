package net.banutama.utamacraft.datagen;

import net.banutama.utamacraft.Utamacraft;
import net.banutama.utamacraft.world.ModWorldgenProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Utamacraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var out = gen.getPackOutput();
        var efh = event.getExistingFileHelper();
        var lp = event.getLookupProvider();

        gen.addProvider(event.includeServer(), new ModWorldgenProvider(out, lp));
    }
}
