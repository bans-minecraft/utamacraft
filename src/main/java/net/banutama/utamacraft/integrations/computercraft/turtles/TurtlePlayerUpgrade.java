package net.banutama.utamacraft.integrations.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.banutama.utamacraft.integrations.computercraft.peripheral.PlayerPeripheral;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TurtlePlayerUpgrade extends PeripheralTurtleUpgrade<PlayerPeripheral> {
    public TurtlePlayerUpgrade(ResourceLocation id, ItemStack item) {
        super(id, item);
    }

    @Override
    protected PlayerPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new PlayerPeripheral(turtle, side);
    }
}
