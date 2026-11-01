package dev.pizzawunsch.advancedhorses.horse;

import net.minecraft.util.RandomSource;

public enum HorseBreed {
    UNKNOWN(0, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0),
    WARMBLOOD(1, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 35),
    THOROUGHBRED(2, 1.12F, -0.15F, -0.1F, 1.15F, 1.15F, 15),
    DRAFT(3, 0.85F, 0.1F, 0.25F, 1.2F, 0.9F, 20),
    PONY(4, 0.88F, 0.2F, 0.05F, 0.7F, 0.85F, 15),
    MUSTANG(5, 1.04F, 0.12F, 0.0F, 0.9F, 1.35F, 15),
    DONKEY(6, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0);

    private static final HorseBreed[] VALUES = values();

    private final int id;
    private final float speed;
    private final float stamina;
    private final float strength;
    private final float hunger;
    private final float taming;
    private final int weight;

    HorseBreed(int id, float speed, float stamina, float strength, float hunger, float taming, int weight) {
        this.id = id;
        this.speed = speed;
        this.stamina = stamina;
        this.strength = strength;
        this.hunger = hunger;
        this.taming = taming;
        this.weight = weight;
    }

    public int id() {
        return this.id;
    }

    public float speed() {
        return this.speed;
    }

    public float stamina() {
        return this.stamina;
    }

    public float strength() {
        return this.strength;
    }

    public float hunger() {
        return this.hunger;
    }

    public float taming() {
        return this.taming;
    }

    public String translationKey() {
        return "advancedhorses.breed." + this.name().toLowerCase();
    }

    public static HorseBreed byId(int id) {
        for (HorseBreed breed : VALUES) {
            if (breed.id == id) {
                return breed;
            }
        }
        return UNKNOWN;
    }

    public static HorseBreed random(RandomSource random) {
        int total = 0;
        for (HorseBreed breed : VALUES) {
            total += breed.weight;
        }
        int roll = random.nextInt(total);
        for (HorseBreed breed : VALUES) {
            roll -= breed.weight;
            if (roll < 0) {
                return breed;
            }
        }
        return WARMBLOOD;
    }
}
