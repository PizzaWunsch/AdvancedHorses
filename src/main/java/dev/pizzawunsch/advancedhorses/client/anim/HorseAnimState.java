package dev.pizzawunsch.advancedhorses.client.anim;

import dev.pizzawunsch.advancedhorses.anim.HorseAnimation;
import dev.pizzawunsch.advancedhorses.horse.HorseGait;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

public final class HorseAnimState {
    public static final RenderStateDataKey<HorseAnimState> KEY = RenderStateDataKey.create(() -> "advancedhorses:anim");

    public final float[] progress = new float[HorseAnimation.values().length];

    public final float[] gaitWeights = new float[HorseGait.values().length];
    public int seed;
    public boolean wild;
    public float phase;
    public float rest;
    public float side;
    public float air;
    public float verticalSpeed;
    public float swim;
    public float turn;
    public float droop;
    public float speed;
    public float tailSwing;
    public float tailLift;
    public boolean lame;

    public float progress(HorseAnimation animation) {
        return this.progress[animation.ordinal()];
    }
}
