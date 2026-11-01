package dev.pizzawunsch.advancedhorses.inventory;

import dev.pizzawunsch.advancedhorses.horse.HorseStats;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class HorseBagSlot extends Slot {
    private final AbstractHorse horse;

    public HorseBagSlot(Container container, int slot, int x, int y, AbstractHorse horse) {
        super(container, slot, x, y);
        this.horse = horse;
    }

    public boolean isLocked() {
        return this.getContainerSlot() >= HorseStats.unlockedSlots(this.horse);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !this.isLocked() && super.mayPlace(stack);
    }
}
