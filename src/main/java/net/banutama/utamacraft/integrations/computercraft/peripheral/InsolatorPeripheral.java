package net.banutama.utamacraft.integrations.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.banutama.utamacraft.block.entity.InsolatorBlockEntity;
import net.banutama.utamacraft.integrations.computercraft.utils.WrapResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class InsolatorPeripheral extends BasePeripheral {
    public static final String PERIPHERAL_TYPE = "insolator";

    protected InsolatorPeripheral(BasePeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
    }

    public InsolatorPeripheral(BlockEntity blockEntity) {
        this(new BlockEntityPeripheralOwner(blockEntity));
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult getState() {
        if (!(owner instanceof BlockEntityPeripheralOwner blockOwner)) {
            return MethodResult.of(null, "Owner of this InsolatorPeripheral is not a BlockEntityPeripheralOwner");
        }

        BlockEntity blockEntity = blockOwner.getBlockEntity();
        if (!(blockEntity instanceof InsolatorBlockEntity insolator)) {
            return MethodResult.of(null,
                    "Owner of this InsolatorPeripheral has a BlockEntityProviderOwner with a BlockEntity that is not an InsolatorBlockEntity");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("active", insolator.getActive());
        result.put("progress", insolator.getProgress());
        result.put("ticks", insolator.getTicks());
        result.put("energy", WrapResult.wrap(insolator.getEnergy()));
        insolator.getInventoryOptional().ifPresent(inventory -> result.put("inventory", WrapResult.wrap(inventory)));

        FluidStack fluidStack = insolator.getFluidTank().getFluid();
        if (!fluidStack.isEmpty()) {
            result.put("fluid", WrapResult.wrap(fluidStack));
        }

        return MethodResult.of(result);
    }
}
