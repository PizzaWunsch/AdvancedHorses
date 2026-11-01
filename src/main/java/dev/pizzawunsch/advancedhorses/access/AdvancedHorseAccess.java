package dev.pizzawunsch.advancedhorses.access;

import dev.pizzawunsch.advancedhorses.anim.HorseAnimationTracker;
import dev.pizzawunsch.advancedhorses.horse.HorseByte;
import dev.pizzawunsch.advancedhorses.horse.HorseStat;
import dev.pizzawunsch.advancedhorses.horse.LifeState;
import dev.pizzawunsch.advancedhorses.inventory.HorseshoeContainer;
import dev.pizzawunsch.advancedhorses.taming.TamingState;
import net.minecraft.world.Container;

public interface AdvancedHorseAccess {
    TamingState advancedhorses$getTamingState();

    int advancedhorses$getSyncedNeeds();

    int advancedhorses$getSyncedProgress();

    void advancedhorses$setSyncedNeeds(int needs);

    void advancedhorses$setSyncedProgress(int progress);

    HorseAnimationTracker advancedhorses$getAnimations();

    LifeState advancedhorses$getLifeState();

    float advancedhorses$getStat(HorseStat stat);

    void advancedhorses$setStat(HorseStat stat, float value);

    int advancedhorses$getByte(HorseByte value);

    void advancedhorses$setByte(HorseByte value, int data);

    String advancedhorses$getPedigree();

    void advancedhorses$setPedigree(String pedigree);

    int advancedhorses$getGear();

    void advancedhorses$setGear(int gear);

    int advancedhorses$getRest();

    void advancedhorses$setRest(int rest);

    Container advancedhorses$getInventory();

    HorseshoeContainer advancedhorses$getShoeContainer();
}
