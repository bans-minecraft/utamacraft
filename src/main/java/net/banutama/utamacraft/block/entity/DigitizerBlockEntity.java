package net.banutama.utamacraft.block.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.banutama.utamacraft.screen.DigitizerMenu;
import net.banutama.utamacraft.util.ModEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class DigitizerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int MATERIALZE_ENERGY_REQUIRED = 1024;
    public static final int REFRESH_ENERGY_REQUIRED = 64;
    public static final int ENERGY_MAX = MATERIALZE_ENERGY_REQUIRED * 64;
    public static final int ENERGY_DRAW = ENERGY_MAX / (20 * 10);

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            DigitizerBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> true;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private final ModEnergyStorage energy = new ModEnergyStorage(ENERGY_MAX, ENERGY_DRAW) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            DigitizerBlockEntity.this.sendUpdate();
        }
    };

    private final LazyOptional<ItemStackHandler> inventoryOptional = LazyOptional.of(() -> inventory);
    private final LazyOptional<ModEnergyStorage> energyOptional = LazyOptional.of(() -> energy);

    public DigitizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIGITIZER.get(), pos, state);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block_entity.utamacraft.digitizer");
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory,
            @NotNull Player player) {
        return new DigitizerMenu(containerId, playerInventory, this);
    }

    @Nullable
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryOptional.cast();
        }

        if (cap == ForgeCapabilities.ENERGY) {
            return energyOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryOptional.invalidate();
        energyOptional.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.put("energy", energy.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        energy.deserializeNBT(nbt.get("energy"));
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

    protected void sendUpdate() {
        setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public ModEnergyStorage getEnergy() {
        return energy;
    }

    public LazyOptional<ItemStackHandler> getInventoryOptional() {
        return inventoryOptional;
    }
}
