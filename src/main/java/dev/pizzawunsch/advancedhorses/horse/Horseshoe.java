package dev.pizzawunsch.advancedhorses.horse;

import dev.pizzawunsch.advancedhorses.registry.ModItems;
import net.minecraft.world.item.ItemStack;

public enum Horseshoe {
    NONE(0, -0.1F, 0.0F, 1.0F),
    IRON(1, 0.0F, 0.0F, 0.8F),
    DIAMOND(2, 0.05F, 0.02F, 0.5F);

    private static final Horseshoe[] VALUES = values();

    private final int id;
    private final float hardGround;
    private final float everywhere;
    private final float injury;

    Horseshoe(int id, float hardGround, float everywhere, float injury) {
        this.id = id;
        this.hardGround = hardGround;
        this.everywhere = everywhere;
        this.injury = injury;
    }

    public int id() {
        return this.id;
    }

    public float hardGround() {
        return this.hardGround;
    }

    public float everywhere() {
        return this.everywhere;
    }

    public float injury() {
        return this.injury;
    }

    public String translationKey() {
        return "advancedhorses.shoes." + this.name().toLowerCase();
    }

    public static Horseshoe byId(int id) {
        for (Horseshoe shoe : VALUES) {
            if (shoe.id == id) {
                return shoe;
            }
        }
        return NONE;
    }

    public static Horseshoe of(ItemStack stack) {
        if (stack.is(ModItems.IRON_HORSESHOE)) {
            return IRON;
        } else if (stack.is(ModItems.DIAMOND_HORSESHOE)) {
            return DIAMOND;
        }
        return NONE;
    }
}
