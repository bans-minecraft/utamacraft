package net.banutama.utamacraft.integrations.computercraft;

import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.core.methods.PeripheralMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BoundMethod {
    private final Object target;
    private final String name;
    private final PeripheralMethod method;

    public BoundMethod(@NotNull Object target, @NotNull String name, @NotNull PeripheralMethod method) {
        this.target = target;
        this.name = name;
        this.method = method;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public MethodResult apply(@NotNull IComputerAccess computer, @NotNull ILuaContext context,
            @NotNull IArguments arguments) throws LuaException {
        return method.apply(target, context, computer, arguments);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof BoundMethod boundMethod)) {
            return false;
        }

        return target.equals(boundMethod.target) && name.equals(boundMethod.name) && method.equals(boundMethod.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, name, method);
    }
}
