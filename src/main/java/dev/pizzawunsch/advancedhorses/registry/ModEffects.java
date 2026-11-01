package dev.pizzawunsch.advancedhorses.registry;

import dev.pizzawunsch.advancedhorses.AdvancedHorses;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

public final class ModEffects {
    public static final int DURATION = 3600;
    public static final int LONG_DURATION = 9600;

    public static final Holder<MobEffect> HORSE_STAMINA = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
        id("horse_stamina"), new MobEffect(MobEffectCategory.BENEFICIAL, 0xFFC940) { });

    public static final Holder<Potion> STAMINA_POTION = Registry.registerForHolder(BuiltInRegistries.POTION,
        id("horse_stamina"), new Potion("horse_stamina", new MobEffectInstance(HORSE_STAMINA, DURATION)));
    public static final Holder<Potion> LONG_STAMINA_POTION = Registry.registerForHolder(BuiltInRegistries.POTION,
        id("long_horse_stamina"), new Potion("horse_stamina", new MobEffectInstance(HORSE_STAMINA, LONG_DURATION)));

    private ModEffects() {
    }

    public static void init() {
    }

    private static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, name);
    }
}
