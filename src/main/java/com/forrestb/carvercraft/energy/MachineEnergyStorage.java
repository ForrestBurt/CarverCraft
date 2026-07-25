package com.forrestb.carvercraft.energy;

import net.neoforged.neoforge.energy.EnergyStorage;

/**
 * A receive-only FE buffer. maxExtract is 0 so cables push power in but can't
 * siphon it back out — standard machine behaviour, and it means a Mekanism or
 * Immersive Engineering cable charges this with no glue code on either side.
 */
public class MachineEnergyStorage extends EnergyStorage {

    public MachineEnergyStorage(int capacity, int maxReceive) {
        super(capacity, maxReceive, 0);
    }

    /** Spend energy directly (machines consume internally, not through extractEnergy). */
    public boolean consume(int amount) {
        if (this.energy < amount) {
            return false;
        }
        this.energy -= amount;
        return true;
    }

    public boolean has(int amount) {
        return this.energy >= amount;
    }

    public void setEnergy(int value) {
        this.energy = Math.max(0, Math.min(this.capacity, value));
    }

    public int getCapacityRaw() {
        return this.capacity;
    }
}
