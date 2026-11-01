package dev.pizzawunsch.advancedhorses.horse;

import dev.pizzawunsch.advancedhorses.AdvancedHorses;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public final class HorseLevel {
    public static final int MAX_LEVEL = 50;

    private static final Identifier JUMP_MODIFIER = Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, "level_jump");
    private static final Identifier HEALTH_MODIFIER = Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, "level_health");
    private static final Identifier SHOE_JUMP_MODIFIER = Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, "shoe_jump");

    private HorseLevel() {
    }

    public static int level(AbstractHorse horse) {
        return Mth.clamp(HorseStats.get(horse, HorseByte.LEVEL), 0, MAX_LEVEL);
    }

    public static float xpForNext(int level) {
        return level >= MAX_LEVEL ? 0.0F : 400.0F + 120.0F * level;
    }

    public static float totalXp(int level) {
        float total = 0.0F;
        for (int i = 0; i < level; i++) {
            total += xpForNext(i);
        }
        return total;
    }

    public static int steps(int level, HorseUpgrade upgrade) {
        int count = HorseUpgrade.values().length;
        return Math.max(0, (Mth.clamp(level, 0, MAX_LEVEL) - upgrade.ordinal() + count - 1) / count);
    }

    public static float bonus(AbstractHorse horse, HorseUpgrade upgrade) {
        return steps(level(horse), upgrade) * upgrade.perStep();
    }

    public static void applyAttributes(AbstractHorse horse) {
        apply(horse, Attributes.JUMP_STRENGTH, JUMP_MODIFIER, bonus(horse, HorseUpgrade.JUMP), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        apply(horse, Attributes.MAX_HEALTH, HEALTH_MODIFIER, bonus(horse, HorseUpgrade.HEALTH), AttributeModifier.Operation.ADD_VALUE);
        apply(horse, Attributes.JUMP_STRENGTH, SHOE_JUMP_MODIFIER, ShoeEnchantments.jumpBonus(horse), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static void apply(AbstractHorse horse, Holder<Attribute> attribute, Identifier id, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = horse.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier current = instance.getModifier(id);
        if (amount <= 0.0) {
            if (current != null) {
                instance.removeModifier(id);
            }
        } else if (current == null || current.amount() != amount) {
            instance.addOrReplacePermanentModifier(new AttributeModifier(id, amount, operation));
        }
    }
}
