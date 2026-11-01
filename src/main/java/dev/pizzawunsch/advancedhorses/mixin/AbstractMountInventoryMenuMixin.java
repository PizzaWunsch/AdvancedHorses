package dev.pizzawunsch.advancedhorses.mixin;

import dev.pizzawunsch.advancedhorses.horse.Horseshoe;
import dev.pizzawunsch.advancedhorses.inventory.HorseshoeSlot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractMountInventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMountInventoryMenu.class)
public abstract class AbstractMountInventoryMenuMixin {
    @Shadow
    @Final
    protected Container mountContainer;

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void advancedhorses$quickMoveHorseshoe(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        AbstractMountInventoryMenu menu = (AbstractMountInventoryMenu) (Object) this;
        int shoeIndex = menu.slots.size() - 1;
        if (shoeIndex < 0 || !(menu.slots.get(shoeIndex) instanceof HorseshoeSlot shoeSlot)) {
            return;
        }

        Slot slot = menu.slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) {
            return;
        }

        AbstractContainerMenuInvoker invoker = (AbstractContainerMenuInvoker) menu;
        int playerStart = 2 + this.mountContainer.getContainerSize();
        int playerEnd = playerStart + 36;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (slotIndex == shoeIndex) {
            if (!invoker.advancedhorses$moveItemStackTo(stack, playerStart, playerEnd, true)) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }
        } else if (slotIndex >= playerStart && slotIndex < playerEnd && Horseshoe.of(stack) != Horseshoe.NONE
            && !shoeSlot.hasItem() && shoeSlot.mayPlace(stack)) {
            if (!invoker.advancedhorses$moveItemStackTo(stack, shoeIndex, shoeIndex + 1, false)) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }
        } else {
            return;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        cir.setReturnValue(original);
    }
}
