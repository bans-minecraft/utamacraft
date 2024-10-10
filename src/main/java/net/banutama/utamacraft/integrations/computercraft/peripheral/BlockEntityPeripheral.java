package net.banutama.utamacraft.integrations.computercraft.peripheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public class BlockEntityPeripheral {
    private final Function<BlockEntity, ? extends IPeripheral> build;
    private final Predicate<BlockEntity> predicate;

    public BlockEntityPeripheral(Function<BlockEntity, ? extends IPeripheral> build, Predicate<BlockEntity> predicate) {
        this.build = build;
        this.predicate = predicate;
    }

    public boolean hasPeripheral(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity == null) {
            return false;
        }

        return predicate.test(entity);
    }

    public @NotNull IPeripheral buildPeripheral(@NotNull Level level, @NotNull BlockPos pos,
            @NotNull Direction direction) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity == null) {
            throw new IllegalArgumentException("No BlockEntity at this location");
        }

        return build.apply(entity);
    }
}
