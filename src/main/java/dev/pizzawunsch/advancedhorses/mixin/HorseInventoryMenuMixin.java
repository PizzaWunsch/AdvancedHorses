package dev.pizzawunsch.advancedhorses.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.pizzawunsch.advancedhorses.inventory.HorseBagSlot;
import dev.pizzawunsch.advancedhorses.inventory.HorseshoeSlot;
import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseInventoryMenu.class)
public abstract class HorseInventoryMenuMixin {
    @WrapOperation(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/world/Container;III)Lnet/minecraft/world/inventory/Slot;"))
    private Slot advancedhorses$createBagSlot(Container container, int slot, int x, int y, Operation<Slot> original,
                                              @Local(argsOnly = true) AbstractHorse horse) {
        if (horse instanceof Horse) {
            return new HorseBagSlot(container, slot, x, y, horse);
        }
        return original.call(container, slot, x, y);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void advancedhorses$addHorseshoeSlot(int containerId, Inventory playerInventory, Container horseInventory, AbstractHorse horse,
                                                 int inventoryColumns, CallbackInfo ci) {
        if (TamingManager.isTameableType(horse)) {
            ((AbstractContainerMenuInvoker) this).advancedhorses$addSlot(
                new HorseshoeSlot(TamingManager.access(horse).advancedhorses$getShoeContainer(), horse));
        }
    }
}
