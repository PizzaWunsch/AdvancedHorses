package dev.pizzawunsch.advancedhorses.taming;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum TamingTreat {
    APPLE(1, Items.APPLE),
    CARROT(2, Items.CARROT),
    SUGAR(3, Items.SUGAR),
    GOLDEN_CARROT(4, Items.GOLDEN_CARROT),
    WHEAT(5, Items.WHEAT);

    private final int id;
    private final Item item;
    private final ItemStack icon;

    TamingTreat(int id, Item item) {
        this.id = id;
        this.item = item;
        this.icon = new ItemStack(item);
    }

    public int id() {
        return this.id;
    }

    public Item item() {
        return this.item;
    }

    public ItemStack icon() {
        return this.icon;
    }

    public static TamingTreat byId(int id) {
        for (TamingTreat treat : values()) {
            if (treat.id == id) {
                return treat;
            }
        }
        return null;
    }
}
