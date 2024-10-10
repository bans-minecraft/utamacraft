package net.banutama.utamacraft.integrations.computercraft.peripheral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.banutama.utamacraft.block.entity.DigitizerBlockEntity;
import net.banutama.utamacraft.integrations.computercraft.peripheral.digitizer.DigitizedCache;
import net.banutama.utamacraft.integrations.computercraft.peripheral.digitizer.DigitizedItem;
import net.banutama.utamacraft.util.InventoryUtils;
import net.banutama.utamacraft.util.LuaConverter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class DigitizerPeripheral extends BasePeripheral {
    public static final String PERIPHERAL_TYPE = "digitizer";

    protected DigitizerPeripheral(BasePeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
    }

    public DigitizerPeripheral(BlockEntity blockEntity) {
        this(new BlockEntityPeripheralOwner(blockEntity));
    }

    private DigitizerBlockEntity getDigitizer() throws LuaException {
        if (!(owner instanceof BlockEntityPeripheralOwner blockOwner)) {
            throw new LuaException("Owner of this DigitizerPeripheral is not a BlockEntityPeripheralOwner");
        }

        if (!(blockOwner.getBlockEntity() instanceof DigitizerBlockEntity digitizer)) {
            throw new LuaException("Owner of this DigitizerPeripheral is not a DigitizerBlockEntity");
        }

        return digitizer;
    }

    @LuaFunction(mainThread = true)
    public final int getEnergy() throws LuaException {
        return getDigitizer().getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final int getEnergyCapacity() throws LuaException {
        return getDigitizer().getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final int size() throws LuaException {
        return getDigitizer().getInventory().getSlots();
    }

    @LuaFunction(mainThread = true)
    public final int getDigitizationCost() {
        return DigitizerBlockEntity.MATERIALZE_ENERGY_REQUIRED;
    }

    @LuaFunction(mainThread = true)
    public final int getRefreshCost() {
        return DigitizerBlockEntity.REFRESH_ENERGY_REQUIRED;
    }

    @LuaFunction(mainThread = true)
    public final int getItemLimit(int slot) throws LuaException {
        var inventory = getDigitizer().getInventory();
        if (slot < 1 || slot > inventory.getSlots()) {
            throw new LuaException(
                    String.format("Slot %d is out of range (%d slots available)", slot, inventory.getSlots()));
        }

        return inventory.getSlotLimit(slot - 1);
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult getItemDetail(int slot) throws LuaException {
        var inventory = getDigitizer().getInventory();
        if (slot < 1 || slot > inventory.getSlots()) {
            throw new LuaException(
                    String.format("Slot %d is out of range (%d slots available)", slot, inventory.getSlots()));
        }

        var stack = inventory.getStackInSlot(slot - 1);
        if (stack.getCount() == 0) {
            return MethodResult.of();
        }

        return MethodResult.of(LuaConverter.getStackDetails(stack));
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult list() throws LuaException {
        var inventory = getDigitizer().getInventory();
        var size = inventory.getSlots();

        Map<Integer, Map<String, ?>> result = new HashMap<>();
        for (var i = 0; i < size; ++i) {
            var stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                result.put(i + 1, LuaConverter.getStackDetails(stack));
            }
        }

        return MethodResult.of(result);
    }

    @LuaFunction(mainThread = true)
    public final @NotNull int pushItems(IComputerAccess computer, @NotNull IArguments arguments) throws LuaException {
        var inventory = getDigitizer().getInventory();
        var toName = arguments.getString(0);
        var fromSlot = arguments.getInt(1);
        var limit = arguments.optInt(2);
        var toSlot = arguments.optInt(3);

        return InventoryUtils.pushItems(computer, inventory, toName, fromSlot, limit, toSlot);
    }

    @LuaFunction(mainThread = true)
    public final @NotNull int pullItems(IComputerAccess computer, @NotNull IArguments arguments) throws LuaException {
        var inventory = getDigitizer().getInventory();
        var fromName = arguments.getString(0);
        var fromSlot = arguments.getInt(1);
        var limit = arguments.optInt(2);
        var toSlot = arguments.optInt(3);

        return InventoryUtils.pullItems(computer, inventory, fromName, fromSlot, limit, toSlot);
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult digitize(@NotNull IArguments arguments) throws LuaException {
        var digitizer = getDigitizer();
        var gameTime = digitizer.getLevel().getGameTime();
        var inventory = digitizer.getInventory();
        var requested = arguments.optInt(0, inventory.getStackInSlot(0).getCount());
        var simulate = arguments.optBoolean(1, false);

        // Figure out how many items we're going to digitize (based on the requested
        // number of items and the number of items available in the inventory slot of
        // the digitizer block). We can use this to compute the cost of the
        // digitization.
        var available = inventory.getStackInSlot(0).getCount();
        var amount = Math.min(requested, available);
        var cost = DigitizerBlockEntity.MATERIALZE_ENERGY_REQUIRED * amount;

        DigitizedItem digitized = null;
        if (!simulate && amount > 0) {
            // As we're not simulating, and there are actually some items that we can
            // digitize, we can deduct the required energy from the digitizer blocks' energy
            // store.
            var energy = digitizer.getEnergy();
            if (!energy.subtractEnergy(cost)) {
                return MethodResult.of(null,
                        String.format("Not enough energy to digitize %d items (require %d, available %d)",
                                amount, cost, energy.getEnergyStored()));
            }

            // Get the extracted item stack from the digitizer and store it as a new
            // digitized item in the cache. We then keep the item for the result structure.
            var extracted = digitizer.getInventory().extractItem(0, amount, false);
            digitized = new DigitizedItem(extracted, gameTime);
            DigitizedCache.getInstance(digitizer.getLevel()).put(digitized);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("simulation", simulate);
        result.put("requested", requested);
        result.put("available", available);
        result.put("count", amount);
        result.put("cost", cost);

        if (digitized != null) {
            result.put("item", digitized.describeItem(gameTime));
        }

        return MethodResult.of(result);
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult materialize(@NotNull IArguments arguments) throws LuaException {
        var id = UUID.fromString(arguments.getString(0));
        var simulate = arguments.optBoolean(2, false);
        var digitizer = getDigitizer();
        var inventory = digitizer.getInventory();
        var level = digitizer.getLevel();
        var cache = DigitizedCache.getInstance(level);
        var gameTime = level.getGameTime();

        // See if we can find the digitised item, and make sure that it has not expired.
        var digitizedItem = cache.take(id);
        if (digitizedItem == null || digitizedItem.isExpired(gameTime)) {
            return MethodResult.of(null, "No digitized item with ID " + id.toString());
        }

        // Store the name of the digitized item for later
        var itemName = ForgeRegistries.ITEMS.getKey(digitizedItem.itemStack.getItem());

        // This is the number of items available in the digitized item stack.
        var available = digitizedItem.itemStack.getCount();

        // The requested number of items to materialize defaults to the number
        // available.
        var requested = arguments.optInt(1, available);
        requested = Math.min(requested, available);

        // Build an ItemStack that contains the number of items that we request to
        // materialize. We're just going to use a copy for now. When we actually do the
        // materialization we'll adjust the digitized item stack.
        var remaining = available - requested;
        var materializing = digitizedItem.itemStack.copy();
        materializing.setCount(requested);

        // Simulate the insertion of the `materializing` stack into the inventory slot.
        // This gives us back an ItemStack that describes the items that were not
        // materialized.
        var unmaterialized = inventory.insertItem(0, materializing, true);

        // Expand the number of items in the unmaterialized stack by those remaining in
        // the digitized item stack.
        unmaterialized.grow(remaining);

        // The actual number of items that would be materialized is the requested amount
        // minus the number of items that were unmaterialized.
        var actual = requested - unmaterialized.getCount();

        // The materialized cost is the number of items that were actually materialized
        // times the cost per item.
        var materializeCost = DigitizerBlockEntity.MATERIALZE_ENERGY_REQUIRED * actual;

        // The refresh cost is the number of items that were not materialized times the
        // refresh cost per item.
        var refreshCost = DigitizerBlockEntity.REFRESH_ENERGY_REQUIRED * unmaterialized.getCount();

        // The final cost is the sum of the materialized cost and the refresh cost.
        var cost = materializeCost + refreshCost;

        if (!simulate) {
            // The user doesn't want to simulate the materialization, so actually deduct the
            // energy needed from the digitizer blocks' energy store and then insert the
            // items into the digitizer's inventory. Any remaining items remain digitized.
            var energy = digitizer.getEnergy();
            if (!energy.subtractEnergy(cost)) {
                return MethodResult.of(null,
                        String.format("Not enough energy to materialize %d items (require %d, available %d)",
                                requested, cost, energy.getEnergyStored()));
            }

            // Now we do the actual materialization: insert the items in the `materializing`
            // stack into the inventory slot. The result is the unmaterialized items.
            unmaterialized = inventory.insertItem(0, materializing, false);

            // Expand the number of items in the unmaterialized stack by those remaining in
            // the digitized item stack.
            unmaterialized.grow(remaining);

            if (unmaterialized.getCount() > 0) {
                // There's still some remainder, so put it back in the cache. Before we do that,
                // we need to update the count to reflect the remainder and refresh the expiry
                // time.
                digitizedItem.itemStack = unmaterialized;
                digitizedItem.refresh(gameTime);
                cache.put(digitizedItem);
            } else {
                // The digitized item stack was depleted: no items remain unmaterialized.
                digitizedItem.itemStack.setCount(0);
            }
        } else {
            // We're simulating, so put the digitized item back in the cache.
            cache.put(digitizedItem);
        }

        // Build up information for the result.
        var result = new HashMap<String, Object>();
        result.put("simulation", simulate);
        result.put("requested", requested);
        result.put("available", available);
        result.put("materialized", actual);
        result.put("remainder", unmaterialized.getCount());
        result.put("cost", cost);
        result.put("materializeCost", materializeCost);
        result.put("refreshCost", refreshCost);

        {
            // Here we need to overwrite the stored `name` property with the original item
            // name. This is because the `describeItem` method will return a `name` property
            // of `minecraft:air` if we've successfully materialized the entire digitized
            // stack.
            var item = digitizedItem.describeItem(gameTime);
            item.put("name", itemName.toString());
            result.put("item", item);
        }

        return MethodResult.of(result);
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult query(@NotNull IArguments arguments) throws LuaException {
        var id = UUID.fromString(arguments.getString(0));
        var level = getDigitizer().getLevel();
        var cache = DigitizedCache.getInstance(level);
        var gameTime = level.getGameTime();

        var digitizedItem = cache.get(id);
        if (digitizedItem != null && !digitizedItem.isExpired(gameTime)) {
            return MethodResult.of(digitizedItem.describeItem(gameTime));
        } else {
            return MethodResult.of(null, "No digitized item with ID " + id.toString());
        }
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult refresh(@NotNull IArguments arguments) throws LuaException {
        var id = UUID.fromString(arguments.getString(0));
        var simulate = arguments.optBoolean(1, false);
        var level = getDigitizer().getLevel();
        var cache = DigitizedCache.getInstance(level);
        var gameTime = level.getGameTime();

        var digitizedItem = cache.get(id);
        if (digitizedItem != null && !digitizedItem.isExpired(gameTime)) {
            var cost = DigitizerBlockEntity.REFRESH_ENERGY_REQUIRED * digitizedItem.itemStack.getCount();
            if (!simulate) {
                var energy = getDigitizer().getEnergy();
                if (!energy.subtractEnergy(cost)) {
                    return MethodResult.of(null,
                            String.format("Not enough energy to refresh %d items (require %d, available %d)",
                                    digitizedItem.itemStack.getCount(), cost, energy.getEnergyStored()));
                }

                digitizedItem.refresh(gameTime);
            }

            var result = new HashMap<String, Object>();
            result.put("simulation", simulate);
            result.put("cost", cost);
            result.put("item", digitizedItem.describeItem(gameTime));

            return MethodResult.of(result);
        } else {
            return MethodResult.of(null, "No digitized item with ID " + id.toString());
        }
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult listDigitized() throws LuaException {
        var level = getDigitizer().getLevel();
        var cache = DigitizedCache.getInstance(level);
        var gameTime = level.getGameTime();

        var result = new ArrayList<Map<String, Object>>();
        cache.forEach(item -> {
            result.add(item.describeItem(gameTime));
        });

        return MethodResult.of(result);
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DigitizerPeripheral otherDigitizer)) {
            return false;
        }

        try {
            return getDigitizer() == otherDigitizer.getDigitizer();
        } catch (LuaException e) {
            return false;
        }
    }
}
