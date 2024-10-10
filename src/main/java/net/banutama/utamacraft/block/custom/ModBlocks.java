package net.banutama.utamacraft.block.custom;

import java.util.function.Supplier;

import net.banutama.utamacraft.Utamacraft;
import net.banutama.utamacraft.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            Utamacraft.MOD_ID);

    public static final RegistryObject<Block> ETHEREAL_GLASS = registerBlock("ethereal_glass", EtherealGlassBlock::new);
    public static final RegistryObject<Block> ETHEREAL_GLASS_TINTED = registerBlock("ethereal_glass_tinted",
            EtherealGlassTintedBlock::new);
    public static final RegistryObject<Block> TUNGSTEN_ORE = registerBlock("tungsten_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(6.0f)
                    .requiresCorrectToolForDrops(),
                    UniformInt.of(3, 7)));
    public static final RegistryObject<Block> DEEPSLATE_TUNGSTEN_ORE = registerBlock("deepslate_tungsten_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(6.0f)
                    .requiresCorrectToolForDrops(),
                    UniformInt.of(3, 7)));
    public static final RegistryObject<Block> TUNGSTEN_BLOCK = registerBlock("tungsten_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(12.0f)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> INSOLATOR = registerBlock("insolator", InsolatorBlock::new);
    public static final RegistryObject<Block> AWARENESS_BLOCK = registerBlock("awareness_block", AwarenessBlock::new);
    public static final RegistryObject<Block> DIGITIZER = registerBlock("digitizer", DigitizerBlock::new);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> registered_block = BLOCKS.register(name, block);
        registerBlockItem(name, registered_block);
        return registered_block;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name,
                () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
