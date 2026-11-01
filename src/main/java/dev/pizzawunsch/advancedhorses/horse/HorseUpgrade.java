package dev.pizzawunsch.advancedhorses.horse;

public enum HorseUpgrade {
    SPEED("advancedhorses.stat.speed", 0.015F),
    JUMP("advancedhorses.stat.jump", 0.025F),
    STAMINA("advancedhorses.stat.stamina", 0.025F),
    STRENGTH("advancedhorses.stat.strength", 0.03F),
    HEALTH("advancedhorses.stat.health", 1.0F);

    private final String translationKey;
    private final float perStep;

    HorseUpgrade(String translationKey, float perStep) {
        this.translationKey = translationKey;
        this.perStep = perStep;
    }

    public String translationKey() {
        return this.translationKey;
    }

    public float perStep() {
        return this.perStep;
    }

    public float max() {
        return this.perStep * HorseLevel.MAX_LEVEL / values().length;
    }

    public static HorseUpgrade forLevel(int level) {
        return values()[(level - 1) % values().length];
    }
}
