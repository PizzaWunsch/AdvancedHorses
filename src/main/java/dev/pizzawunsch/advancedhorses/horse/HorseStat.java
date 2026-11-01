package dev.pizzawunsch.advancedhorses.horse;

public enum HorseStat {
    HUNGER(HorseStats.MAX_HUNGER),
    STAMINA(-1.0F),
    STRENGTH(-1.0F),
    XP(0.0F),
    ENERGY(HorseStats.MAX_ENERGY),
    BOND(0.0F);

    private final float defaultValue;

    HorseStat(float defaultValue) {
        this.defaultValue = defaultValue;
    }

    public float defaultValue() {
        return this.defaultValue;
    }

    public String saveKey() {
        return "AdvancedHorses" + this.name();
    }
}
