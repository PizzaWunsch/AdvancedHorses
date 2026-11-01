package dev.pizzawunsch.advancedhorses.taming;

public final class TamingState {
    public final float[] points = new float[TamingData.SLOTS];
    public long lastProgressTime;
    public long feedCooldownUntil;
    public long petCooldownUntil;
    public long groomCooldownUntil;
    public boolean wasInWater;

    public void reset() {
        for (int i = 0; i < this.points.length; i++) {
            this.points[i] = 0.0F;
        }
        this.lastProgressTime = 0L;
    }
}
