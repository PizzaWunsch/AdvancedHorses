package dev.pizzawunsch.advancedhorses.horse;

import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import java.util.Locale;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class HorseStats {
    public static final float MAX_HUNGER = 100.0F;
    public static final float MAX_ENERGY = 100.0F;
    public static final int MAX_SLOTS = 6;

    public static final float MAX_SPEED_BONUS = 0.08F;
    public static final float MAX_STAMINA_BONUS = 0.12F;
    public static final float MAX_STRENGTH_BONUS = 0.15F;

    public static final double MIN_SPEED = 0.1125;
    public static final double MAX_SPEED = 0.3375;

    public static final long TICKS_PER_YEAR = 24000L;

    private HorseStats() {
    }

    public static float get(AbstractHorse horse, HorseStat stat) {
        return TamingManager.access(horse).advancedhorses$getStat(stat);
    }

    public static void set(AbstractHorse horse, HorseStat stat, float value) {
        TamingManager.access(horse).advancedhorses$setStat(stat, value);
    }

    public static int get(AbstractHorse horse, HorseByte value) {
        return TamingManager.access(horse).advancedhorses$getByte(value);
    }

    public static void set(AbstractHorse horse, HorseByte value, int data) {
        TamingManager.access(horse).advancedhorses$setByte(value, data);
    }

    public static boolean hasFlag(AbstractHorse horse, int flag) {
        return (get(horse, HorseByte.FLAGS) & flag) != 0;
    }

    public static void setFlag(AbstractHorse horse, int flag, boolean value) {
        int flags = get(horse, HorseByte.FLAGS);
        int updated = value ? flags | flag : flags & ~flag;
        if (updated != flags) {
            set(horse, HorseByte.FLAGS, updated);
        }
    }

    public static HorseBreed breed(AbstractHorse horse) {
        return HorseBreed.byId(get(horse, HorseByte.BREED));
    }

    public static HorseTrait trait(AbstractHorse horse) {
        return HorseTrait.byId(get(horse, HorseByte.TRAIT));
    }

    public static Horseshoe shoes(AbstractHorse horse) {
        return Horseshoe.byId(get(horse, HorseByte.SHOES));
    }

    public static void ensureGenetics(AbstractHorse horse) {
        RandomSource random = horse.getRandom();
        if (get(horse, HorseStat.STAMINA) < 0.0F) {
            set(horse, HorseStat.STAMINA, (random.nextFloat() + random.nextFloat()) / 2.0F);
        }
        if (get(horse, HorseStat.STRENGTH) < 0.0F) {
            set(horse, HorseStat.STRENGTH, (random.nextFloat() + random.nextFloat()) / 2.0F);
        }
        if (get(horse, HorseByte.BREED) == 0) {
            set(horse, HorseByte.BREED, (horse instanceof Horse ? HorseBreed.random(random) : HorseBreed.DONKEY).id());
        }
        if (get(horse, HorseByte.TRAIT) == 0) {
            set(horse, HorseByte.TRAIT, HorseTrait.random(random).id());
        }
    }

    public static void inherit(AbstractHorse parent, AgeableMob partner, AbstractHorse baby) {
        if (!(partner instanceof AbstractHorse other)) {
            return;
        }
        ensureGenetics(parent);
        ensureGenetics(other);
        RandomSource random = parent.getRandom();
        for (HorseStat stat : new HorseStat[]{HorseStat.STAMINA, HorseStat.STRENGTH}) {
            double value = AbstractHorse.createOffspringAttribute(get(parent, stat), get(other, stat), 0.0, 1.0, random);
            set(baby, stat, (float) value);
        }

        HorseBreed breed;
        if (!(baby instanceof Horse)) {
            breed = HorseBreed.DONKEY;
        } else {
            HorseBreed a = breed(parent);
            HorseBreed b = breed(other);
            int roll = random.nextInt(100);
            if (a != b && roll < 10 || a == HorseBreed.DONKEY || b == HorseBreed.DONKEY) {
                breed = HorseBreed.WARMBLOOD;
            } else {
                breed = roll < 55 ? a : b;
            }
        }
        set(baby, HorseByte.BREED, breed.id());

        int traitRoll = random.nextInt(100);
        HorseTrait trait = traitRoll < 40 ? trait(parent) : traitRoll < 80 ? trait(other) : HorseTrait.random(random);
        set(baby, HorseByte.TRAIT, trait.id());

        TamingManager.access(baby).advancedhorses$setPedigree(Pedigree.create(parent, other));
    }

    public static float genetic(AbstractHorse horse, HorseStat stat) {
        return Math.max(0.0F, get(horse, stat));
    }

    public static float stamina(AbstractHorse horse) {
        return Mth.clamp(genetic(horse, HorseStat.STAMINA) + breed(horse).stamina()
            + MAX_STAMINA_BONUS * get(horse, HorseStat.TRAINED_STAMINA), 0.0F, 1.0F);
    }

    public static float strength(AbstractHorse horse) {
        return Mth.clamp(genetic(horse, HorseStat.STRENGTH) + breed(horse).strength()
            + MAX_STRENGTH_BONUS * get(horse, HorseStat.TRAINED_STRENGTH), 0.0F, 1.0F);
    }

    public static float bond(AbstractHorse horse) {
        return Mth.clamp(get(horse, HorseStat.BOND), 0.0F, 1.0F);
    }

    public static float speedBonus(AbstractHorse horse) {
        return MAX_SPEED_BONUS * get(horse, HorseStat.TRAINED_SPEED);
    }

    public static int ageYears(AbstractHorse horse) {
        return get(horse, HorseByte.AGE);
    }

    public static float ageSpeedFactor(AbstractHorse horse) {
        if (!hasFlag(horse, HorseByte.FLAG_AGING)) {
            return 1.0F;
        }
        int age = ageYears(horse);
        return age >= 24 ? 0.85F : age >= 18 ? 0.92F : 1.0F;
    }

    public static float ageTrainingFactor(AbstractHorse horse) {
        if (!hasFlag(horse, HorseByte.FLAG_AGING)) {
            return 1.0F;
        }
        int age = ageYears(horse);
        return age < 4 ? 1.3F : age > 14 ? 0.7F : 1.0F;
    }

    public static float speedFactor(AbstractHorse horse) {
        return (1.0F + speedBonus(horse)) * breed(horse).speed() * ageSpeedFactor(horse);
    }

    public static double speed(AbstractHorse horse) {
        return horse.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedFactor(horse);
    }

    public static int unlockedSlots(AbstractHorse horse) {
        if (!(horse instanceof Horse)) {
            return Integer.MAX_VALUE;
        }
        return Math.min(MAX_SLOTS, Mth.floor(strength(horse) * 100.0F / 16.0F));
    }

    public static double blocksPerSecond(double speedAttribute) {
        return speedAttribute * 42.16;
    }

    public static double jumpHeight(double jumpStrength) {
        return -0.1817584952 * jumpStrength * jumpStrength * jumpStrength
            + 3.689713992 * jumpStrength * jumpStrength
            + 2.128599134 * jumpStrength
            - 0.343930367;
    }

    public static String summary(AbstractHorse horse) {
        return String.format(Locale.ROOT, "%.1f;%d;%d;%d", blocksPerSecond(speed(horse)),
            Math.round(stamina(horse) * 100.0F), Math.round(strength(horse) * 100.0F), breed(horse).id());
    }

    public static float foodValue(ItemStack stack) {
        if (stack.is(Items.SUGAR)) {
            return 3.0F;
        } else if (stack.is(Items.RED_MUSHROOM)) {
            return 5.0F;
        } else if (stack.is(Items.WHEAT)) {
            return 6.0F;
        } else if (stack.is(Items.APPLE) || stack.is(Items.CARROT)) {
            return 8.0F;
        } else if (stack.is(Items.GOLDEN_CARROT)) {
            return 15.0F;
        } else if (stack.is(Items.GOLDEN_APPLE)) {
            return 25.0F;
        } else if (stack.is(Items.HAY_BLOCK)) {
            return 35.0F;
        } else if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            return 40.0F;
        }
        return 0.0F;
    }
}
