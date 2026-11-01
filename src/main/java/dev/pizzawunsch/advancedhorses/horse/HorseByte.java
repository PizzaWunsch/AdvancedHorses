package dev.pizzawunsch.advancedhorses.horse;

public enum HorseByte {
    BREED(0),
    TRAIT(0),
    SHOES(0),
    FLAGS(0),
    TERRAIN(100),
    AGE(0),
    LEVEL(0),
    SHOE_SPEED(0),
    SHOE_JUMP(0),
    SHOE_STAMINA(0);

    public static final int FLAG_WINDED = 1;
    public static final int FLAG_LAME = 2;
    public static final int FLAG_AGING = 4;
    public static final int FLAG_STAMINA_POTION = 8;

    private final int defaultValue;

    HorseByte(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int defaultValue() {
        return this.defaultValue;
    }

    public String saveKey() {
        return "AdvancedHorses" + this.name();
    }
}
