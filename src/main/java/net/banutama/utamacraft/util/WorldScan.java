package net.banutama.utamacraft.util;

import java.util.function.BiConsumer;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class WorldScan {
    public static int manhattanDistance(BlockPos from, BlockPos to) {
        return Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY())
                + Math.abs(from.getZ() - to.getZ());
    }

    public enum Side {
        All, Up, Down, North, East, South, West
    }

    // Generate a bounding box for the given origin, radius and block side.
    private static BoundingBox getScanBounds(BlockPos origin, int r, Side side) {
        int ox = origin.getX();
        int oy = origin.getY();
        int oz = origin.getZ();

        int x1 = ox - r;
        int y1 = oy - r;
        int z1 = oz - r;

        int x2 = ox + r;
        int y2 = oy + r;
        int z2 = oz + r;

        switch (side) {
            case Up:
                y1 = oy;
                break;
            case Down:
                y2 = oy;
                break;
            case North:
                z1 = oz;
                break;
            case East:
                x2 = ox;
                break;
            case South:
                z2 = oz;
                break;
            case West:
                x1 = ox;
                break;
            default:
                break;
        }

        return new BoundingBox(x1, y1, z1, x2, y2, z2);
    }

    public static void scanBlocks(Level level, BlockPos origin, int r, Side side,
            BiConsumer<BlockState, BlockPos> consumer) {
        BoundingBox bounds = getScanBounds(origin, r, side);
        int total = 0;

        for (int x = bounds.minX(); x < bounds.maxX(); ++x) {
            for (int y = bounds.minY(); y < bounds.maxY(); ++y) {
                for (int z = bounds.minZ(); z < bounds.maxZ(); ++z) {
                    total += 1;
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (!state.isAir()) {
                        consumer.accept(state, pos);
                    }
                }
            }
        }

        LogUtils.getLogger().info("Scanned {} blocks", total);
    }
}
