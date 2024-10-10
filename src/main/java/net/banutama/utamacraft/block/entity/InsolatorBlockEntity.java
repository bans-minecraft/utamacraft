package net.banutama.utamacraft.block.entity;

import net.banutama.utamacraft.block.custom.InsolatorBlock;
import net.banutama.utamacraft.recipe.InsolatorRecipe;
import net.banutama.utamacraft.recipe.ModRecipes;
import net.banutama.utamacraft.screen.InsolatorMenu;
import net.banutama.utamacraft.util.ModEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InsolatorBlockEntity extends BlockEntity implements MenuProvider {
    public static final int ENERGY_REQUIRED = 32;

    private final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            InsolatorBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
                case 1 -> true;
                case 2 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private final ModEnergyStorage energy = new ModEnergyStorage(60000, 256) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            InsolatorBlockEntity.this.sendUpdate();
        }
    };

    private final FluidTank fluid = new FluidTank(64000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            InsolatorBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isFluidValid(@NotNull FluidStack stack) {
            return stack.getFluid() == Fluids.WATER;
        }
    };

    private final LazyOptional<ItemStackHandler> inventoryOptional = LazyOptional.of(() -> this.inventory);
    private final LazyOptional<ModEnergyStorage> energyOptional = LazyOptional.of(() -> energy);
    private final LazyOptional<FluidTank> fluidOptional = LazyOptional.of(() -> fluid);

    private int progress = 0;
    private int ticks = 0;
    private boolean active = false;

    public InsolatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.INSOLATOR.get(), pPos, pBlockState);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block_entity.utamacraft.insolator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory,
            @NotNull Player pPlayer) {
        return new InsolatorMenu(pContainerId, pPlayerInventory, this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyOptional.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryOptional.cast();
        }

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryOptional.invalidate();
        energyOptional.invalidate();
        fluidOptional.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.put("fluid", fluid.writeToNBT(new CompoundTag()));
        nbt.put("energy", energy.serializeNBT());
        nbt.putInt("progress", progress);
        nbt.putInt("ticks", ticks);
        nbt.putBoolean("active", active);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        fluid.readFromNBT(nbt.getCompound("fluid"));
        energy.deserializeNBT(nbt.get("energy"));
        progress = nbt.getInt("progress");
        ticks = nbt.getInt("ticks");
        active = nbt.getBoolean("active");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void serverTick(@NotNull Level level, BlockPos pos, BlockState state, InsolatorBlockEntity entity) {
        if (level.isClientSide()) {
            return;
        }

        boolean newActive = entity.active;

        Optional<InsolatorRecipe> optionalInsolatorRecipe = entity.getRecipe();
        if (optionalInsolatorRecipe.isEmpty()) {
            newActive = false;
        } else {
            InsolatorRecipe insolatorRecipe = optionalInsolatorRecipe.get();
            if (entity.canCraft(insolatorRecipe) && entity.hasEnoughEnergy(insolatorRecipe)) {
                if (entity.progress == 0) {
                    entity.ticks = insolatorRecipe.getTicks();
                }

                ++entity.progress;
                newActive = true;

                entity.energy.extractEnergy(ENERGY_REQUIRED, false);
                setChanged(level, pos, state);

                if (entity.progress >= insolatorRecipe.getTicks()) {
                    entity.craftItem(insolatorRecipe);
                }
            } else {
                entity.resetProgress(insolatorRecipe.getTicks());
                newActive = false;
                setChanged(level, pos, state);
            }
        }

        if (newActive != entity.active) {
            entity.active = newActive;
            state = state.setValue(InsolatorBlock.ACTIVE, newActive);
            level.setBlock(pos, state, 3);
        }

        ItemStack fluid = entity.inventory.getStackInSlot(0);
        if (fluid.getCount() > 0) {
            fluid.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
                // Remove as much as we need, but no more than a bucket
                int amount = Math.min(entity.fluid.getSpace(), 1000);

                // Simulate removal of that amount from the IFluidHandlerItem
                FluidStack stack = handler.drain(amount, IFluidHandler.FluidAction.SIMULATE);

                // Ensure that the fluid we would use is valid
                if (entity.fluid.isFluidValid(stack)) {
                    // Remove the amount of fluid from the IFluidHandlerItem
                    stack = handler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                    // Fill our fluid tank with the fluid stack we drained from the
                    // IFluidHandlerItem
                    entity.fluid.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                    // Extract the current item from the fluid slot?
                    entity.inventory.extractItem(0, 1, false);
                    entity.inventory.insertItem(0, handler.getContainer(), false);
                }
            });
        }
    }

    private void resetProgress(int newTicks) {
        this.progress = 0;
        this.ticks = newTicks;
    }

    private void craftItem(InsolatorRecipe recipe) {
        // Drain the amount of fluid specified in our recipe from the fluid tank
        fluid.drain(recipe.getFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);

        // Remove an item from the input slot.
        inventory.extractItem(1, 1, false);

        // Insert the recipe output in to the output slot.
        ItemStack output = inventory.getStackInSlot(2);
        if (output.isEmpty()) {
            inventory.setStackInSlot(2, recipe.getResultItem(null).copy());
        } else {
            output.grow(recipe.getResultItem(null).getCount());
        }

        resetProgress(recipe.getTicks());
    }

    private boolean hasEnoughEnergy(InsolatorRecipe recipe) {
        return energy.getEnergyStored() >= ENERGY_REQUIRED * recipe.getTicks();
    }

    private boolean canCraft(InsolatorRecipe recipe) {
        // Make sure that the correct fluid is in the tank.
        if (!fluid.getFluid().equals(recipe.getFluid())) {
            return false;
        }

        // Ensure that we have enough fluid
        if (fluid.getFluidAmount() < recipe.getFluid().getAmount()) {
            return false;
        }

        ItemStack output = inventory.getStackInSlot(2);

        // If the output isn't empty, and the item in the output is different to the
        // output of the recipe, we cannot process.
        if (!output.isEmpty() && output.getItem() != recipe.getResultItem(null).getItem()) {
            return false;
        }

        // Make sure that the output is not saturated.
        return output.getCount() + recipe.getResultItem(null).getCount() <= output.getMaxStackSize();
    }

    private Optional<InsolatorRecipe> getRecipe() {
        if (level == null) {
            return Optional.empty();
        }

        SimpleContainer inventory = new SimpleContainer(this.inventory.getSlots());
        for (int slot = 0; slot < this.inventory.getSlots(); ++slot) {
            inventory.setItem(slot, this.inventory.getStackInSlot(slot));
        }

        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.INSOLATOR_RECIPE_TYPE.get(), inventory, level);
    }

    private void sendUpdate() {
        setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public LazyOptional<ItemStackHandler> getInventoryOptional() {
        return this.inventoryOptional;
    }

    public FluidTank getFluidTank() {
        return this.fluid;
    }

    public EnergyStorage getEnergy() {
        return this.energy;
    }

    public boolean getActive() {
        return this.active;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getTicks() {
        return this.ticks;
    }

    public void dumpFluids() {
        this.fluid.drain(this.fluid.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
    }
}
