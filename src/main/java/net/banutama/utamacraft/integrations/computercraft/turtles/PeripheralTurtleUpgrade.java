package net.banutama.utamacraft.integrations.computercraft.turtles;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import net.banutama.utamacraft.integrations.computercraft.peripheral.BasePeripheral;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PeripheralTurtleUpgrade<T extends BasePeripheral> extends AbstractTurtleUpgrade {
    protected PeripheralTurtleUpgrade(ResourceLocation id, ItemStack item) {
        super(id, TurtleUpgradeType.PERIPHERAL, String.format("turtle.utamacraft.%s", id.getPath()), item);
    }

    protected abstract T buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side);

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return buildPeripheral(turtle, side);
    }
}
