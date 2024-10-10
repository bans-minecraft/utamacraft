package net.banutama.utamacraft.integrations.computercraft.peripheral;

import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityPeripheralOwner extends BasePeripheralOwner {
    private final BlockEntity blockEntity;

    public BlockEntityPeripheralOwner(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
