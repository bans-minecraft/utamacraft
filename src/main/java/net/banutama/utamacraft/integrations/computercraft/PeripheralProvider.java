package net.banutama.utamacraft.integrations.computercraft;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.banutama.utamacraft.integrations.computercraft.peripheral.BlockEntityPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class PeripheralProvider implements IPeripheralProvider {
    private final List<BlockEntityPeripheral> blockEntityPeripherals = new ArrayList<>();

    public void registerBlockPeripheral(Function<BlockEntity, ? extends IPeripheral> build,
            Predicate<BlockEntity> predicate) {
        blockEntityPeripherals.add(new BlockEntityPeripheral(build, predicate));
    }

    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level world, @NotNull BlockPos pos,
            @NotNull Direction side) {
        for (BlockEntityPeripheral peripheral : blockEntityPeripherals) {
            if (peripheral.hasPeripheral(world, pos, side)) {
                return LazyOptional.of(() -> peripheral.buildPeripheral(world, pos, side));
            }
        }

        return LazyOptional.empty();
    }
}
