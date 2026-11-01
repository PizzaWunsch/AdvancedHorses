package dev.pizzawunsch.advancedhorses.anim;

import net.minecraft.util.Mth;

public final class GaitPattern {
    public static final float[][] PHASE = {
        {0.0F, 0.5F, 0.75F, 0.25F},
        {0.0F, 0.5F, 0.75F, 0.25F},
        {0.0F, 0.5F, 0.5F, 0.0F},
        {0.0F, 0.25F, 0.7F, 0.0F},
        {0.0F, 0.15F, 0.55F, 0.7F},
        {0.0F, 0.12F, 0.5F, 0.62F}
    };

    private GaitPattern() {
    }

    public static float legPhase(float cycle, int gait, int leg) {
        return cycle + Mth.PI + PHASE[gait][leg] * Mth.TWO_PI;
    }

    public static boolean isSwinging(float cycle, int gait, int leg) {
        return Mth.sin(legPhase(cycle, gait, leg)) > 0.0F;
    }
}
