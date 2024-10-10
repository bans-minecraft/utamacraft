package net.banutama.utamacraft.item;

import dan200.computercraft.shared.ModRegistry;
import net.banutama.utamacraft.CCRegistration;
import net.banutama.utamacraft.Utamacraft;
import net.banutama.utamacraft.block.custom.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Utamacraft.MOD_ID);

    public static final RegistryObject<CreativeModeTab> UTAMACRAFT_TAB = CREATIVE_MODE_TABS.register("utamacraft_tab", () -> {
        return CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BULB.get()))
                .title(Component.translatable("creativetab.utamacraft_tab"))
                .displayItems((params, output) -> {
                    output.accept(ModItems.BULB.get());
                    output.accept(ModItems.FIBER_GLASS.get());
                    output.accept(ModItems.PCB.get());
                    output.accept(ModItems.FIRE_WARD.get());
                    output.accept(ModItems.PLAYER_PERIPHERAL.get());
                    output.accept(ModItems.TELEPORTER_PERIPHERAL.get());
                    output.accept(ModItems.TUNGSTEN_INGOT.get());
                    output.accept(ModItems.TUNGSTEN_RAW.get());

                    output.accept(ModBlocks.AWARENESS_BLOCK.get());
                    output.accept(ModBlocks.DEEPSLATE_TUNGSTEN_ORE.get());
                    output.accept(ModBlocks.TUNGSTEN_BLOCK.get());
                    output.accept(ModBlocks.DIGITIZER.get());
                    output.accept(ModBlocks.ETHEREAL_GLASS.get());
                    output.accept(ModBlocks.ETHEREAL_GLASS_TINTED.get());
                    output.accept(ModBlocks.INSOLATOR.get());
                    output.accept(ModBlocks.TUNGSTEN_ORE.get());

                    output.acceptAll(turtleWithPeripheral(CCRegistration.ID.PLAYER_TURTLE));
                    output.acceptAll(turtleWithPeripheral(CCRegistration.ID.TELEPORTER_TURTLE));
                })
                .build();
    });

    private static Collection<ItemStack> turtleWithPeripheral(ResourceLocation peripheral) {
        ItemStack turtleStack = new ItemStack(ModRegistry.Items.TURTLE_NORMAL.get());
        turtleStack.getOrCreateTag().putString("RightUpgrade", peripheral.toString());

        ItemStack advanctedTurtleStack = new ItemStack(ModRegistry.Items.TURTLE_ADVANCED.get());
        advanctedTurtleStack.getOrCreateTag().putString("RightUpgrade", peripheral.toString());

        return Set.of(turtleStack, advanctedTurtleStack);
    }

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
