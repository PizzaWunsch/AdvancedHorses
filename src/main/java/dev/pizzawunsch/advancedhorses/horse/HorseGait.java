package dev.pizzawunsch.advancedhorses.horse;

import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public enum HorseGait {
    STAND(0.0F, 0.0F),
    WALK(0.3F, 0.008F),
    TROT(0.55F, 0.016F),
    CANTER(0.75F, 0.028F),
    GALLOP(1.0F, 0.045F),
    RACE(1.22F, 0.08F);

    private static final HorseGait[] VALUES = values();

    public static int clientControlledId = -1;
    public static int clientGear;

    private final float speedFactor;
    private final float hungerPerBlock;

    HorseGait(float speedFactor, float hungerPerBlock) {
        this.speedFactor = speedFactor;
        this.hungerPerBlock = hungerPerBlock;
    }

    public float speedFactor() {
        return this.speedFactor;
    }

    public float hungerPerBlock() {
        return this.hungerPerBlock;
    }

    public String translationKey() {
        return "advancedhorses.gait." + this.name().toLowerCase();
    }

    public static HorseGait byIndex(int index) {
        return VALUES[Math.clamp(index, 0, VALUES.length - 1)];
    }

    public static HorseGait current(AbstractHorse horse) {
        if (horse.level().isClientSide() && horse.getId() == clientControlledId) {
            return byIndex(clientGear);
        }
        return byIndex(TamingManager.access(horse).advancedhorses$getGear());
    }

    public static HorseGait maxAllowed(AbstractHorse horse) {
        HorseGait max = RACE;
        if (horse.isTamed()) {
            float hunger = HorseStats.get(horse, HorseStat.HUNGER);
            if (hunger <= 0.0F) {
                max = WALK;
            } else if (hunger < 10.0F) {
                max = TROT;
            } else if (hunger < 25.0F) {
                max = CANTER;
            }
        }
        if (HorseStats.hasFlag(horse, HorseByte.FLAG_LAME) && max.ordinal() > TROT.ordinal()) {
            max = TROT;
        }
        if (HorseStats.hasFlag(horse, HorseByte.FLAG_WINDED) && max.ordinal() > CANTER.ordinal()) {
            max = CANTER;
        }
        return max;
    }
}
