package dev.pizzawunsch.advancedhorses.registry;

import dev.pizzawunsch.advancedhorses.AdvancedHorses;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public final class ModRules {
    public static final GameRule<Boolean> HUNGER = bool("hunger", true);
    public static final GameRule<Integer> HUNGER_RATE = percent("hunger_rate", 100, 0, 1000);
    public static final GameRule<Integer> TAMING_EFFORT = percent("taming_effort", 100, 10, 1000);
    public static final GameRule<Integer> XP_RATE = percent("xp_rate", 100, 0, 1000);
    public static final GameRule<Boolean> STAMINA = bool("stamina", true);
    public static final GameRule<Boolean> INJURIES = bool("injuries", true);
    public static final GameRule<Boolean> AGING = bool("aging", false);
    public static final GameRule<Boolean> TERRAIN_SPEED = bool("terrain_speed", true);
    public static final GameRule<Boolean> RESTING = bool("resting", true);

    private ModRules() {
    }

    public static void init() {
    }

    private static GameRule<Boolean> bool(String name, boolean defaultValue) {
        return GameRuleBuilder.forBoolean(defaultValue)
            .category(GameRuleCategory.MOBS)
            .buildAndRegister(Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, name));
    }

    private static GameRule<Integer> percent(String name, int defaultValue, int min, int max) {
        return GameRuleBuilder.forInteger(defaultValue)
            .range(min, max)
            .category(GameRuleCategory.MOBS)
            .buildAndRegister(Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, name));
    }

    public static boolean enabled(Level level, GameRule<Boolean> rule) {
        return level instanceof ServerLevel serverLevel && serverLevel.getGameRules().get(rule);
    }

    public static float factor(Level level, GameRule<Integer> rule) {
        return level instanceof ServerLevel serverLevel ? serverLevel.getGameRules().get(rule) / 100.0F : 1.0F;
    }
}
