package dev.pizzawunsch.advancedhorses.horse;

import dev.pizzawunsch.advancedhorses.AdvancedHorses;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class ShoeEnchantments {
    public static final ResourceKey<Enchantment> FLEETFOOT = key("fleetfoot");
    public static final ResourceKey<Enchantment> SPRING_HOOF = key("spring_hoof");
    public static final ResourceKey<Enchantment> ENDURANCE = key("endurance");

    public static final float SPEED_PER_LEVEL = 0.03F;
    public static final float JUMP_PER_LEVEL = 0.05F;
    public static final float STAMINA_PER_LEVEL = 0.05F;

    private ShoeEnchantments() {
    }

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, name));
    }

    public static int level(ItemStack stack, ResourceKey<Enchantment> key) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static void update(AbstractHorse horse, ItemStack shoes) {
        set(horse, HorseByte.SHOE_SPEED, level(shoes, FLEETFOOT));
        set(horse, HorseByte.SHOE_JUMP, level(shoes, SPRING_HOOF));
        set(horse, HorseByte.SHOE_STAMINA, level(shoes, ENDURANCE));
    }

    private static void set(AbstractHorse horse, HorseByte value, int level) {
        if (HorseStats.get(horse, value) != level) {
            HorseStats.set(horse, value, level);
        }
    }

    public static float speedBonus(AbstractHorse horse) {
        return SPEED_PER_LEVEL * HorseStats.get(horse, HorseByte.SHOE_SPEED);
    }

    public static float jumpBonus(AbstractHorse horse) {
        return JUMP_PER_LEVEL * HorseStats.get(horse, HorseByte.SHOE_JUMP);
    }

    public static float staminaBonus(AbstractHorse horse) {
        return STAMINA_PER_LEVEL * HorseStats.get(horse, HorseByte.SHOE_STAMINA);
    }
}
