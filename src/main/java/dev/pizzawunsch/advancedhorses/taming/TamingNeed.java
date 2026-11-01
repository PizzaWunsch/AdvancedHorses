package dev.pizzawunsch.advancedhorses.taming;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum TamingNeed {
    HUNGER(1, 12.0F, new ItemStack(Items.WHEAT)),
    TREAT(2, 4.0F, new ItemStack(Items.APPLE)),
    AFFECTION(3, 6.0F, new ItemStack(Items.POPPY)),
    GROOMING(4, 8.0F, new ItemStack(Items.BRUSH)),
    TRUST(5, 4.0F, new ItemStack(Items.SADDLE)),
    COMPANY(6, 40.0F, new ItemStack(Items.CLOCK));

    private final int id;
    private final float required;
    private final ItemStack icon;

    TamingNeed(int id, float required, ItemStack icon) {
        this.id = id;
        this.required = required;
        this.icon = icon;
    }

    public int id() {
        return this.id;
    }

    public float required(boolean bred) {
        return bred ? Math.max(1.0F, (float) Math.ceil(this.required * 0.5F)) : this.required;
    }

    public ItemStack icon() {
        return this.icon;
    }

    public String translationKey() {
        return "advancedhorses.need." + this.name().toLowerCase();
    }

    public String hintKey() {
        return "advancedhorses.need." + this.name().toLowerCase() + ".hint";
    }

    public static TamingNeed byId(int id) {
        for (TamingNeed need : values()) {
            if (need.id == id) {
                return need;
            }
        }
        return null;
    }
}
