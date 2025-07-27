package net.banutama.utamacraft.integrations.computercraft.peripheral;

import com.mojang.logging.LogUtils;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.banutama.utamacraft.integrations.computercraft.turtles.TurtlePlayerCache;
import net.banutama.utamacraft.integrations.computercraft.utils.WrapResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;
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

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult getTrades() {
        if (!(owner instanceof TurtlePeripheralOwner turtleOwner)) {
            LOGGER.info("Owner of this PlayerPeripheral is not a TurtlePeripheralOwner");
            return MethodResult.of();
        }

        MerchantOffers offers = TurtlePlayerCache.withPlayer(turtleOwner.getTurtle(), player -> player.getTrades(3));
        if (offers == null) {
            LOGGER.info("No trades available for the player");
            return MethodResult.of();
        }

        LOGGER.info("Retrieved {} trades for the player", offers.size());
        for (var offer : offers) {
            LOGGER.info("Trade: {} for {} with max uses {}", offer.getBaseCostA(), offer.getResult(),
                    offer.getMaxUses());
        }

        return MethodResult.of(WrapResult.wrap(offers));
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult makeTrade(int tradeIndex, int slotA, int slotB, int slotOutput) {
        if (tradeIndex <= 0) {
            LOGGER.info("Trade index must be greater than 0, received: {}", tradeIndex);
            return MethodResult.of(null, "Trade index must be greater than 0");
        }

        if (!(owner instanceof TurtlePeripheralOwner turtleOwner)) {
            LOGGER.info("Owner of this PlayerPeripheral is not a TurtlePeripheralOwner");
            return MethodResult.of();
        }

        ITurtleAccess turtle = turtleOwner.getTurtle();
        var inventory = turtle.getInventory();

        // The trade slot 'slotA' is 1-based in the API, so it must be in the range of
        // the turtl's
        // inventory size.
        if (slotA < 1 || slotA > inventory.getContainerSize()) {
            LOGGER.info("Invalid slotA: {}. Must be between 1 and {}", slotA, inventory.getContainerSize());
            return MethodResult.of(null, String.format("Invalid slotA: %d. Must be between 1 and %d",
                    slotA, inventory.getContainerSize()));
        }

        // The trade slot 'slotB' is also 1-based, but can be 0 to indicate no second
        // offer. If
        // slotB is provided, it must be within the inventory size.
        if (slotB < 0 || (slotB > 0 && slotB > inventory.getContainerSize())) {
            LOGGER.info("Invalid slotB: {}. Must be between 0 and {}", slotB, inventory.getContainerSize());
            return MethodResult.of(null, String.format("Invalid slotB: %d. Must be between 0 and %d",
                    slotB, inventory.getContainerSize()));
        }

        // The output slot 'slotOutput' is also 1-based, and must be within the
        // inventory size.
        if (slotOutput < 1 || slotOutput > inventory.getContainerSize()) {
            LOGGER.info("Invalid slotOutput: {}. Must be between 1 and {}", slotOutput, inventory.getContainerSize());
            return MethodResult.of(null, String.format("Invalid slotOutput: %d. Must be between 1 and %d",
                    slotOutput, inventory.getContainerSize()));
        }

        // Get the targetted entity.
        var entity = TurtlePlayerCache.withPlayer(turtle, player -> player.getTargetedEntity(3));
        if (!(entity instanceof Villager villager)) {
            LOGGER.info("Targeted entity is not a Villager: {}", entity);
            return MethodResult.of(null, "Targeted entity is not a Villager");
        }

        // Get the trades from the targetted villager or whatever.
        var offers = villager.getOffers();
        if (offers == null || offers.isEmpty()) {
            LOGGER.info("No trades available for the player");
            return MethodResult.of(null, "No trades available for the player");
        }

        // See if the trade index is valid.
        if (tradeIndex < 1 || tradeIndex > offers.size()) {
            LOGGER.info("Trade index {} is out of bounds for the player's offers", tradeIndex);
            return MethodResult.of(null, String.format("Trade index %d is out of bounds for the player's offers",
                    tradeIndex));
        }

        // Get the trade offer from the list of offers.
        var offer = offers.get(tradeIndex - 1); // tradeIndex is 1-based, so we subtract 1.

        // Get the items from the inventory for the trade.
        ItemStack offerA = inventory.getItem(slotA - 1);
        ItemStack offerB = slotB > 0 ? turtle.getInventory().getItem(slotB - 1) : ItemStack.EMPTY;

        // Check if the offer can be taken with the provided items.
        if (!offer.satisfiedBy(offerA, offerB)) {
            LOGGER.info("Offer {} does not match the provided items: slotA={}, slotB={}", tradeIndex, slotA, slotB);
            return MethodResult.of(null, "Offer does not match the provided trade items");
        }

        var tradeResult = offer.getResult();

        // Make sure that the output slot can accept the result of the trade.
        ItemStack currentOutput = inventory.getItem(slotOutput - 1);
        if (!currentOutput.isEmpty()) {
            if (!currentOutput.is(tradeResult.getItem())) {
                LOGGER.info("Output slot already has a different item type: {} vs {}", currentOutput.getItem(),
                        tradeResult.getItem());
                return MethodResult.of(null, "Output slot already has a different item type");
            }

            // Make sure that the output slot can accept the result count of the trade.
            var resultCount = tradeResult.getCount() + currentOutput.getCount();
            if (resultCount > currentOutput.getMaxStackSize()) {
                LOGGER.info("Output slot cannot accept the result count: {} + {} > {}", currentOutput.getCount(),
                        tradeResult.getCount(), currentOutput.getMaxStackSize());
                return MethodResult.of(null, "Not enough space in output slot");
            }

        }

        // Make the actual trade.
        offer.take(offerA, offerB);
        var result = offer.assemble();
        villager.notifyTrade(offer);

        if (currentOutput.isEmpty()) {
            inventory.setItem(slotOutput - 1, result);
        } else {
            currentOutput.setCount(currentOutput.getCount() + result.getCount());
        }

        LOGGER.info("Trade result placed in output slot {}", slotOutput);
        return MethodResult.of(WrapResult.wrap(result));
    }
}
