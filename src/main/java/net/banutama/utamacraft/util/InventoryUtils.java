package net.banutama.utamacraft.util;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

public class InventoryUtils {
    public static IItemHandler getItemHandler(@Nullable Object object) {
        if (object instanceof ICapabilityProvider capabilityProvider) {
            LazyOptional<IItemHandler> capability = capabilityProvider.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (capability.isPresent()) {
                return capability.orElseThrow(NullPointerException::new);
            }
        }

        if (object instanceof IItemHandler itemHandler) {
            return itemHandler;
        }

        if (object instanceof Container container) {
            return new InvWrapper(container);
        }

        return null;
    }

    public static IItemHandler getItemHandlerByName(@NotNull IComputerAccess access, String name) throws LuaException {
        IPeripheral location = access.getAvailablePeripheral(name);
        if (location == null) {
            return null;
        }

        return getItemHandler(location.getTarget());
    }

    public static int moveItem(IItemHandler from, IItemHandler to, int fromSlot, int toSlot, int count) {
        int amount = count, transferrable = 0;
        for (int i = fromSlot == -1 ? 0 : fromSlot; i < (fromSlot == -1 ? from.getSlots() : fromSlot + 1); i++) {
            ItemStack extracted = from.extractItem(i, amount - transferrable, true);
            if (extracted.isEmpty()) {
                continue;
            }

            ItemStack inserted;
            if (toSlot == -1) {
                inserted = ItemHandlerHelper.insertItem(to, extracted, false);
            } else {
                inserted = to.insertItem(toSlot, extracted, false);
            }

            amount -= inserted.getCount();
            transferrable += from.extractItem(i, extracted.getCount() - inserted.getCount(), false).getCount();
            if (transferrable >= count) {
                break;
            }
        }

        return transferrable;
    }

    public static int pushItems(IComputerAccess computer, IItemHandler from, String toName, int fromSlot,
            Optional<Integer> limit, Optional<Integer> toSlot) throws LuaException {
        var location = computer.getAvailablePeripheral(toName);
        if (location == null) {
            throw new LuaException("Target '" + toName + "' does not exist");
        }

        var to = getItemHandler(location.getTarget());
        if (to == null) {
            throw new LuaException("Target '" + toName + "' is not an inventory");
        }

        if (fromSlot < 1 || fromSlot > from.getSlots()) {
            throw new LuaException("From slot out of range (" + from.getSlots() + ")");
        }

        if (toSlot.isPresent()) {
            if (toSlot.get() < 1 || toSlot.get() > to.getSlots()) {
                throw new LuaException("To slot out of range (" + to.getSlots() + ")");
            }
        }

        int actualLimit = limit.orElse(Integer.MAX_VALUE);
        if (actualLimit <= 0) {
            return 0;
        }

        return moveItem(from, to, fromSlot - 1, toSlot.orElse(0) - 1, actualLimit);
    }

    public static int pullItems(IComputerAccess computer, IItemHandler to, String fromName, int fromSlot,
            Optional<Integer> limit, Optional<Integer> toSlot) throws LuaException {
        var location = computer.getAvailablePeripheral(fromName);
        if (location == null) {
            throw new LuaException("Target '" + fromName + "' does not exist");
        }

        var from = getItemHandler(location.getTarget());
        if (from == null) {
            throw new LuaException("Target '" + fromName + "' is not an inventory");
        }

        if (fromSlot < 1 || fromSlot > from.getSlots()) {
            throw new LuaException("From slot out of range (" + from.getSlots() + ")");
        }

        if (toSlot.isPresent()) {
            if (toSlot.get() < 1 || toSlot.get() > to.getSlots()) {
                throw new LuaException("To slot out of range (" + to.getSlots() + ")");
            }
        }

        int actualLimit = limit.orElse(Integer.MAX_VALUE);
        if (actualLimit <= 0) {
            return 0;
        }

        return moveItem(from, to, fromSlot - 1, toSlot.orElse(0) - 1, actualLimit);
    }
}
