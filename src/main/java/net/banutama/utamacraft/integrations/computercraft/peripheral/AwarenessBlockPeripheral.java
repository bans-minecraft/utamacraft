package net.banutama.utamacraft.integrations.computercraft.peripheral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.banutama.utamacraft.block.entity.AwarenessBlockEntity;
import net.banutama.utamacraft.util.LuaConverter;
import net.banutama.utamacraft.util.WorldScan;
import net.banutama.utamacraft.util.WorldScan.Side;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class AwarenessBlockPeripheral extends BasePeripheral {
    public static final String PERIPHERAL_TYPE = "awareness_block";

    protected AwarenessBlockPeripheral(BasePeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
    }

    public AwarenessBlockPeripheral(BlockEntity blockEntity) {
        this(new BlockEntityPeripheralOwner(blockEntity));
    }

    private AwarenessBlockEntity getBlock() {
        if (!(owner instanceof BlockEntityPeripheralOwner blockOwner)) {
            return null;
        }

        BlockEntity blockEntity = blockOwner.getBlockEntity();
        if (!(blockEntity instanceof AwarenessBlockEntity block)) {
            return null;
        }

        return block;
    }

    @LuaFunction(mainThread = true)
    public final int getEnergy() {
        return getBlock().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    @LuaFunction(mainThread = true)
    public final int getEnergyCapacity() {
        return getBlock().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult getCost(@NotNull IArguments arguments) throws LuaException {
        int radius = arguments.getInt(0);
        if (radius < 1) {
            return MethodResult.of(null, "Radius must be greater than zero");
        }

        String sideString = arguments.optString(1, "all");
        Side side = parseSide(sideString);

        int cost = AwarenessBlockEntity.getCost(radius, side);
        return MethodResult.of(cost);
    }

    @LuaFunction(mainThread = true)
    public final @NotNull MethodResult scan(@NotNull IArguments arguments) throws LuaException {
        int radius = arguments.getInt(0);
        if (radius < 1) {
            return MethodResult.of(null, "Radius must be greater than zero");
        }

        String sideString = arguments.optString(1, "all");
        Side side = parseSide(sideString);

        if (!(owner instanceof BlockEntityPeripheralOwner blockOwner)) {
            return MethodResult.of(null, "Owner of this AwarenessBlockPeripheral is not a BlockEntityPeripheralOwner");
        }

        BlockEntity blockEntity = blockOwner.getBlockEntity();
        if (!(blockEntity instanceof AwarenessBlockEntity block)) {
            return MethodResult.of(null,
                    "Owner of this AwarenessBlockEntity has a BlockEntityProviderOwner with a BlockEntity that is not an AwarenessBlockEntity");
        }

        IEnergyStorage energy = block.getCapability(ForgeCapabilities.ENERGY).orElse(null);
        if (energy == null) {
            return MethodResult.of(null, "BlockEntity does not have an IEnergyStorage capability");
        }

        int energyCost = AwarenessBlockEntity.getCost(radius, side);
        int energyUsed = energy.extractEnergy(energyCost, false);

        if (energyUsed != energyCost) {
            return MethodResult.of(null, String.format("Not enough energy available: %d/%d", energyUsed, energyCost));
        }

        Level level = blockEntity.getLevel();
        BlockPos origin = blockEntity.getBlockPos();

        Map<String, Object> result = new HashMap<>();

        {
            Map<String, Object> originMap = new HashMap<>();
            originMap.put("x", origin.getX());
            originMap.put("y", origin.getY());
            originMap.put("z", origin.getZ());
            result.put("origin", originMap);
        }

        List<Map<String, ?>> blocks = new ArrayList<>();
        WorldScan.scanBlocks(level, origin, radius, side, (state, pos) -> {
            blocks.add(describeBlock(level, origin, state, pos));
        });

        {
            Map<String, Object> energyMap = new HashMap<>();
            energyMap.put("used", energyUsed);
            energyMap.put("cost", energyCost);
            result.put("energy", energyMap);
        }

        result.put("blocks", blocks);
        result.put("side", sideString);

        return MethodResult.of(result);
    }

    private static Side parseSide(String sideString) throws LuaException {
        switch (sideString) {
            case "all":
                return Side.All;
            case "up":
                return Side.Up;
            case "down":
                return Side.Down;
            case "north":
                return Side.North;
            case "east":
                return Side.East;
            case "south":
                return Side.South;
            case "west":
                return Side.West;
            default:
                throw new LuaException("Invalid side");
        }
    }

    private static HashMap<String, Object> describeBlock(Level level, BlockPos origin, BlockState state, BlockPos pos) {
        HashMap<String, Object> blockInfo = new HashMap<>(5);

        blockInfo.put("x", pos.getX() - origin.getX());
        blockInfo.put("y", pos.getY() - origin.getY());
        blockInfo.put("z", pos.getZ() - origin.getZ());

        Block block = state.getBlock();
        ResourceLocation name = ForgeRegistries.BLOCKS.getKey(block);
        blockInfo.put("name", name == null ? "unknown" : name.toString());
        blockInfo.put("tags", LuaConverter.tagsToList(() -> block.builtInRegistryHolder().tags()));

        describeBlockEntity(blockInfo, level, pos);

        return blockInfo;
    }

    private static void describeBlockEntity(HashMap<String, Object> blockInfo, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity == null) {
            return;
        }

        describeInventories(blockInfo, blockEntity);
    }

    private static void describeInventories(HashMap<String, Object> blockInfo, BlockEntity entity) {
        IItemHandler mainInventory = entity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);

        if (mainInventory != null) {
            blockInfo.put("inventory", describeInventory(mainInventory));
        }
    }

    private static HashMap<String, Object> describeInventory(IItemHandler inventory) {
        List<Map<String, ?>> slots = new ArrayList<>();
        for (int i = 0; i < inventory.getSlots(); ++i) {
            HashMap<String, Object> itemInfo = new HashMap<>(2);
            ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(inventory.getStackInSlot(i).getItem());
            itemInfo.put("name", itemName == null ? "unknown" : itemName.toString());
            itemInfo.put("count", inventory.getStackInSlot(i).getCount());
            slots.add(itemInfo);
        }

        HashMap<String, Object> inventoryInfo = new HashMap<>(2);
        inventoryInfo.put("size", inventory.getSlots());
        inventoryInfo.put("slots", slots);
        return inventoryInfo;
    }
}
