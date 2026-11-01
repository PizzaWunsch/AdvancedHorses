package dev.pizzawunsch.advancedhorses.horse;

import java.util.UUID;
import net.minecraft.core.BlockPos;

public final class LifeState {
    public float hunger = HorseStats.MAX_HUNGER;
    public float energy = HorseStats.MAX_ENERGY;
    public float bond;
    public float xp;
    public boolean airborne;
    public double jumpStartY;
    public double jumpPeakY;
    public int restTicks;
    public double lastX;
    public double lastZ;
    public boolean hasLastPos;
    public boolean hungerWarned;
    public long birthTime = -1L;
    public int lameTicks;
    public float shoeWear;
    public UUID calledBy;
    public long calledUntil;
    public BlockPos trough;
    public long troughCooldownUntil;
    public long groomCooldownUntil;
    public double unshodHardDistance;
}
