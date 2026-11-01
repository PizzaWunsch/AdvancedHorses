package dev.pizzawunsch.advancedhorses.horse;

import net.minecraft.util.RandomSource;

public enum HorseTrait {
    UNKNOWN(0, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F),
    CALM(1, 0.8F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F),
    NERVOUS(2, 1.25F, 1.1F, 1.0F, 1.0F, 1.05F, 1.2F),
    GREEDY(3, 1.0F, 1.0F, 1.25F, 1.3F, 1.0F, 1.0F),
    LAZY(4, 1.0F, 0.75F, 0.85F, 1.0F, 1.0F, 1.0F),
    EAGER(5, 1.0F, 1.25F, 1.0F, 1.0F, 1.1F, 1.0F),
    BRAVE(6, 1.1F, 1.0F, 1.0F, 1.0F, 0.95F, 0.5F);

    private static final HorseTrait[] VALUES = values();

    private final int id;
    private final float taming;
    private final float training;
    private final float hunger;
    private final float food;
    private final float energy;
    private final float injury;

    HorseTrait(int id, float taming, float training, float hunger, float food, float energy, float injury) {
        this.id = id;
        this.taming = taming;
        this.training = training;
        this.hunger = hunger;
        this.food = food;
        this.energy = energy;
        this.injury = injury;
    }

    public int id() {
        return this.id;
    }

    public float taming() {
        return this.taming;
    }

    public float training() {
        return this.training;
    }

    public float hunger() {
        return this.hunger;
    }

    public float food() {
        return this.food;
    }

    public float energy() {
        return this.energy;
    }

    public float injury() {
        return this.injury;
    }

    public String translationKey() {
        return "advancedhorses.trait." + this.name().toLowerCase();
    }

    public static HorseTrait byId(int id) {
        for (HorseTrait trait : VALUES) {
            if (trait.id == id) {
                return trait;
            }
        }
        return UNKNOWN;
    }

    public static HorseTrait random(RandomSource random) {
        return VALUES[1 + random.nextInt(VALUES.length - 1)];
    }
}
