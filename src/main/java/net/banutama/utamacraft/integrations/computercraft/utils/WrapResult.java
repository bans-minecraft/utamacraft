package net.banutama.utamacraft.integrations.computercraft.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;

public class WrapResult {
    public static Map<String, Object> wrap(BlockPos pos) {
        var result = new HashMap<String, Object>(3);
        result.put("x", pos.getX());
        result.put("y", pos.getY());
        result.put("z", pos.getZ());
        return result;
    }

    public static Map<String, Object> wrap(FluidStack fluid) {
        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("name", getName(fluid.getFluid()).toString());
        wrapped.put("amount", fluid.getAmount());
        return wrapped;
    }

    public static Map<String, Object> wrap(IEnergyStorage energy) {
        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("stored", energy.getEnergyStored());
        wrapped.put("maxStored", energy.getMaxEnergyStored());
        return wrapped;
    }

    public static Map<String, Object> wrap(ItemStack stack) {
        Map<String, Object> wrapped = new HashMap<>(3);
        wrapped.put("item", getName(stack.getItem()).toString());
        wrapped.put("count", stack.getCount());
        wrapped.put("maxStackSize", stack.getMaxStackSize());
        return wrapped;
    }

    public static Map<String, Object> wrap(IItemHandler itemHandler) {
        List<Object> slots = new ArrayList<>(itemHandler.getSlots());
        for (int slot = 0; slot < itemHandler.getSlots(); ++slot) {
            Map<String, Object> inner = wrap(itemHandler.getStackInSlot(slot));
            inner.put("slotLimit", itemHandler.getSlotLimit(slot));
            slots.add(inner);
        }

        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("size", itemHandler.getSlots());
        wrapped.put("slots", slots);
        return wrapped;
    }

    public static Map<String, Object> wrap(IFluidHandler fluidHandler) {
        List<Object> tanks = new ArrayList<>(fluidHandler.getTanks());
        for (int tank = 0; tank < fluidHandler.getTanks(); ++tank) {
            var fluid = wrap(fluidHandler.getFluidInTank(tank));
            fluid.put("capacity", fluidHandler.getTankCapacity(tank));
            tanks.add(fluid);
        }

        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("size", fluidHandler.getTanks());
        wrapped.put("tanks", tanks);
        return wrapped;
    }

    public static Map<String, Object> wrap(BlockState state) {
        Block block = state.getBlock();
        var result = wrap(block);
        return result;
    }

    public static Map<String, Object> wrap(BlockEntity entity) {
        Map<String, Object> wrapped = new HashMap<>();

        IItemHandler inventory = entity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
        if (inventory != null) {
            wrapped.put("inventory", wrap(inventory));
        }

        IEnergyStorage energy = entity.getCapability(ForgeCapabilities.ENERGY).resolve().orElse(null);
        if (energy != null) {
            wrapped.put("energy", wrap(energy));
        }

        IFluidHandler fluid = entity.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().orElse(null);
        if (fluid != null) {
            wrapped.put("fluid", wrap(fluid));
        }

        return wrapped;
    }

    public static Map<String, Object> wrap(Block block) {
        Map<String, Object> wrapped = new HashMap<>();
        wrapped.put("name", getName(block).toString());
        return wrapped;
    }

    public static Map<String, Object> wrap(Level level, BlockPos pos) {
        var wrapped = wrap(level.getBlockState(pos));
        wrapped.put("pos", wrap(pos));

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity != null) {
            wrapped.put("entity", wrap(entity));
        }

        return wrapped;
    }

    public static ResourceLocation getName(Item item) {
        return getName(ForgeRegistries.ITEMS, item);
    }

    public static ResourceLocation getName(Fluid fluid) {
        return getName(ForgeRegistries.FLUIDS, fluid);
    }

    public static ResourceLocation getName(Block block) {
        return getName(ForgeRegistries.BLOCKS, block);
    }

    public static <T> ResourceLocation getName(IForgeRegistry<T> registry, T element) {
        return registry.getKey(element);
    }
}
