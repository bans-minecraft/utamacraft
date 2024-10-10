package net.banutama.utamacraft.integrations.computercraft.peripheral;

import com.mojang.logging.LogUtils;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.banutama.utamacraft.integrations.computercraft.turtles.TurtlePlayerCache;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class PlayerPeripheral extends BasePeripheral {
    public static final String PERIPHERAL_TYPE = "player";
    private static final Logger LOGGER = LogUtils.getLogger();

    protected PlayerPeripheral(BasePeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
    }

    public PlayerPeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(new TurtlePeripheralOwner(turtle, side));
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult use() {
        if (!(owner instanceof TurtlePeripheralOwner turtleOwner)) {
            LOGGER.info("Owner of this PlayerPeripheral is not a TurtlePeripheralOwner");
            return MethodResult.of();
        }

        InteractionResult result = TurtlePlayerCache.withPlayer(turtleOwner.getTurtle(),
                player -> player.use(5, true, false, null));

        return MethodResult.of(result.name());
    }
}
