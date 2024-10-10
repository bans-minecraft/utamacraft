package net.banutama.utamacraft.integrations.computercraft.peripheral;

import java.util.HashMap;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.banutama.utamacraft.integrations.computercraft.utils.WrapResult;
import net.banutama.utamacraft.util.WorldScan;
import net.minecraft.core.BlockPos;

public class TeleporterPeripheral extends BasePeripheral {
    public static final int COST_PER_BLOCK = 1;
    public static final String PERIPHERAL_TYPE = "teleporter";
    public static final Logger LOGGER = LogUtils.getLogger();

    protected TeleporterPeripheral(BasePeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
    }

    public TeleporterPeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(new TurtlePeripheralOwner(turtle, side));
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult getPosition() {
        if (!(owner instanceof TurtlePeripheralOwner turtleOwner)) {
            LOGGER.info("Owner of this TeleporterPeripheral is not a TurtlePeripheralOwner");
            return MethodResult.of();
        }

        var turtle = turtleOwner.getTurtle();
        var currentPos = turtle.getPosition();
        return MethodResult.of(WrapResult.wrap(currentPos));
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult teleport(@NotNull IArguments arguments) throws LuaException {
        if (!(owner instanceof TurtlePeripheralOwner turtleOwner)) {
            LOGGER.info("Owner of this TeleporterPeripheral is not a TurtlePeripheralOwner");
            return MethodResult.of();
        }

        var simulate = arguments.optBoolean(3, false);
        var turtle = turtleOwner.getTurtle();
        var level = turtle.getLevel();
        var currentPos = turtle.getPosition();
        var targetPos = new BlockPos(arguments.getInt(0), arguments.getInt(1), arguments.getInt(2));
        var distance = WorldScan.manhattanDistance(currentPos, targetPos);
        var cost = distance * COST_PER_BLOCK;

        var result = new HashMap<String, Object>();
        result.put("from", WrapResult.wrap(currentPos));
        result.put("to", WrapResult.wrap(targetPos));
        result.put("distance", distance);
        result.put("cost", cost);
        result.put("target", WrapResult.wrap(level, targetPos));

        if (!simulate) {
            if (!level.getBlockState(targetPos).isAir()) {
                return MethodResult.of(null, "target is not empty (air)");
            }

            if (!turtle.consumeFuel(cost)) {
                return MethodResult.of(null, "not enough fuel");
            }

            if (!turtle.teleportTo(level, targetPos)) {
                return MethodResult.of(null, "teleport failed");
            }
        }

        return MethodResult.of(result);
    }
}
