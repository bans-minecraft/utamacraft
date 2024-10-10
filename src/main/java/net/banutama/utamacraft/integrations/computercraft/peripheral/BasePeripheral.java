package net.banutama.utamacraft.integrations.computercraft.peripheral;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BasePeripheral implements IPeripheral {
    protected final String type;
    protected final BasePeripheralOwner owner;
    protected final Set<IComputerAccess> computers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    protected BasePeripheral(String type, BasePeripheralOwner owner) {
        this.type = type;
        this.owner = owner;
    }

    @NotNull
    @Override
    public String getType() {
        return type;
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        this.computers.add(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        this.computers.remove(computer);
    }

    @Nullable
    @Override
    public Object getTarget() {
        return owner;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return Objects.equals(this, other);
    }
}
