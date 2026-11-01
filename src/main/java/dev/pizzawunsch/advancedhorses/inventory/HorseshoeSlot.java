package dev.pizzawunsch.advancedhorses.inventory;

import dev.pizzawunsch.advancedhorses.AdvancedHorses;
import dev.pizzawunsch.advancedhorses.horse.Horseshoe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class HorseshoeSlot extends Slot {
    public static final int X = 8;
    public static final int Y = 54;
    private static final Identifier EMPTY_ICON = Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, "container/slot/horseshoe");

    private final AbstractHorse horse;

    public HorseshoeSlot(Container container, AbstractHorse horse) {
        super(container, 0, X, Y);
        this.horse = horse;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return Horseshoe.of(stack) != Horseshoe.NONE && !this.horse.isBaby();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public Identifier getNoItemIcon() {
        return EMPTY_ICON;
    }
}
