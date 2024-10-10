package net.banutama.utamacraft.block.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.banutama.utamacraft.util.ModEnergyStorage;
import net.banutama.utamacraft.util.WorldScan.Side;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

public class AwarenessBlockEntity extends BlockEntity {
    private static final int ENERGY_REQUIRED = 4;

    public static int getCost(int radius, Side side) {
        int blocks = (int) Math.pow(radius * 2, 3);
        if (side != Side.All) {
            blocks >>= 1;
        }

        return blocks * ENERGY_REQUIRED;
    }

    private final ModEnergyStorage energy = new ModEnergyStorage(60000, 256, 256000) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            AwarenessBlockEntity.this.sendUpdate();
        }
    };

    private final LazyOptional<ModEnergyStorage> energyOptional = LazyOptional.of(() -> energy);

    public AwarenessBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AWARENESS_BLOCK.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyOptional.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("energy", energy.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        energy.deserializeNBT(nbt.get("energy"));
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void sendUpdate() {
        setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
}
