package net.banutama.utamacraft.integrations.computercraft.turtles;

import org.jetbrains.annotations.NotNull;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.banutama.utamacraft.integrations.computercraft.peripheral.TeleporterPeripheral;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class TurtleTeleporterUpgrade extends PeripheralTurtleUpgrade<TeleporterPeripheral> {
    public TurtleTeleporterUpgrade(ResourceLocation id, ItemStack item) {
        super(id, item);
    }

    @Override
    protected TeleporterPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new TeleporterPeripheral(turtle, side);
    }
}
