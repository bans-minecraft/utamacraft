package net.banutama.utamacraft.util;

import net.minecraftforge.energy.EnergyStorage;

public abstract class ModEnergyStorage extends EnergyStorage {
    public ModEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public ModEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (extracted != 0) {
            onEnergyChanged();
        }

        return extracted;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (received != 0) {
            onEnergyChanged();
        }

        return received;
    }

    public int setEnergy(int energy) {
        this.energy = energy;
        return energy;
    }

    public boolean subtractEnergy(int energy) {
        if (this.energy >= energy) {
            this.energy -= energy;
            this.onEnergyChanged();
            return true;
        }

        return false;
    }

    public abstract void onEnergyChanged();
}
