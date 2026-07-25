package com.forrestb.carvercraft.energy;

import net.neoforged.neoforge.energy.IEnergyStorage;

/** A bottomless FE source. Creative only. */
public class CreativeEnergyStorage implements IEnergyStorage {
    public static final int CAPACITY = Integer.MAX_VALUE;

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        return 0; // already full, forever
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return toExtract;
    }

    @Override
    public int getEnergyStored() {
        return CAPACITY;
    }

    @Override
    public int getMaxEnergyStored() {
        return CAPACITY;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
