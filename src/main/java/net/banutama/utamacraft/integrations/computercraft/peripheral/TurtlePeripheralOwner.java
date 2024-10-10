package net.banutama.utamacraft.integrations.computercraft.peripheral;

import org.jetbrains.annotations.NotNull;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;

public class TurtlePeripheralOwner extends BasePeripheralOwner {
    private final ITurtleAccess turtle;
    private final TurtleSide side;

    public TurtlePeripheralOwner(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        this.turtle = turtle;
        this.side = side;
    }

    public @NotNull ITurtleAccess getTurtle() {
        return turtle;
    }

    public @NotNull TurtleSide getTurtleSide() {
        return side;
    }
}
