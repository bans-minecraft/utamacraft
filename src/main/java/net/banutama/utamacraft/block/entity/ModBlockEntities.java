package net.banutama.utamacraft.block.entity;

import net.banutama.utamacraft.Utamacraft;
import net.banutama.utamacraft.block.custom.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(ForgeRegistries.BLOCK_ENTITY_TYPES, Utamacraft.MOD_ID);

    public static final RegistryObject<BlockEntityType<InsolatorBlockEntity>> INSOLATOR = BLOCK_ENTITIES.register(
            "insolator",
            () -> BlockEntityType.Builder.of(InsolatorBlockEntity::new, ModBlocks.INSOLATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<AwarenessBlockEntity>> AWARENESS_BLOCK = BLOCK_ENTITIES.register(
            "awareness_block",
            () -> BlockEntityType.Builder.of(AwarenessBlockEntity::new, ModBlocks.AWARENESS_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<DigitizerBlockEntity>> DIGITIZER = BLOCK_ENTITIES.register(
            "digitizer_block",
            () -> BlockEntityType.Builder.of(DigitizerBlockEntity::new, ModBlocks.DIGITIZER.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
