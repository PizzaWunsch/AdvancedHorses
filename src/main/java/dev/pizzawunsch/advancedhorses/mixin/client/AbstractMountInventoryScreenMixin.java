package dev.pizzawunsch.advancedhorses.mixin.client;

import dev.pizzawunsch.advancedhorses.client.hud.HorseStatsPanel;
import dev.pizzawunsch.advancedhorses.inventory.HorseBagSlot;
import dev.pizzawunsch.advancedhorses.inventory.HorseshoeSlot;
import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractMountInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractMountInventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMountInventoryScreen.class)
public abstract class AbstractMountInventoryScreenMixin extends AbstractContainerScreen<AbstractMountInventoryMenu> {
    @Shadow
    @Final
    protected LivingEntity mount;

    @Shadow
    protected abstract void extractSlot(GuiGraphicsExtractor graphics, int x, int y);

    private AbstractMountInventoryScreenMixin(AbstractMountInventoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Inject(method = "extractBackground", at = @At("TAIL"))
    private void advancedhorses$extractStats(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!(this.mount instanceof AbstractHorse horse) || !TamingManager.isTameableType(horse)) {
            return;
        }

        this.extractSlot(graphics, this.leftPos + HorseshoeSlot.X - 1, this.topPos + HorseshoeSlot.Y - 1);
        for (Slot slot : this.menu.slots) {
            if (slot instanceof HorseBagSlot bagSlot && bagSlot.isLocked()) {
                HorseStatsPanel.drawLockedSlot(graphics, this.leftPos + slot.x, this.topPos + slot.y);
            }
        }

        int rightX = this.leftPos + 176 + 4;
        int leftX = this.leftPos - HorseStatsPanel.PEDIGREE_WIDTH - 4;
        boolean rightFits = rightX + HorseStatsPanel.WIDTH <= this.width - 2;
        boolean leftFits = leftX >= 2;
        if (rightFits) {
            HorseStatsPanel.draw(graphics, this.font, horse, rightX, this.topPos);
            if (leftFits) {
                HorseStatsPanel.drawPedigree(graphics, this.font, horse, leftX, this.topPos);
            }
        } else if (leftFits) {
            HorseStatsPanel.draw(graphics, this.font, horse, this.leftPos - HorseStatsPanel.WIDTH - 4, this.topPos);
        }
    }
}
