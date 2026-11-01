package dev.pizzawunsch.advancedhorses.client.gait;

import dev.pizzawunsch.advancedhorses.horse.HorseByte;
import dev.pizzawunsch.advancedhorses.horse.HorseGait;
import dev.pizzawunsch.advancedhorses.horse.HorseStat;
import dev.pizzawunsch.advancedhorses.horse.HorseStats;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public final class GaitHud implements HudElement {
    private static final int WIDTH = 84;
    private static final int PIP_ON = 0xFFE0A030;
    private static final int PIP_OFF = 0xFF3A322C;
    private static final int PIP_BLOCKED = 0xFF6A2020;

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || HorseGait.clientControlledId == -1
            || !(minecraft.player.getVehicle() instanceof AbstractHorse horse)) {
            return;
        }

        Font font = minecraft.font;
        HorseGait gait = HorseGait.byIndex(HorseGait.clientGear);
        HorseGait max = HorseGait.maxAllowed(horse);
        int x = graphics.guiWidth() / 2 - 91 - 36 - WIDTH;
        int y = graphics.guiHeight() - 27;
        if (x < 2) {
            return;
        }

        graphics.fill(x, y, x + WIDTH, y + 25, 0x90101010);
        float energy = HorseStats.get(horse, HorseStat.ENERGY) / HorseStats.MAX_ENERGY;
        boolean winded = HorseStats.hasFlag(horse, HorseByte.FLAG_WINDED);
        int energyW = WIDTH - 8;
        graphics.fill(x + 4, y + 21, x + 4 + energyW, y + 23, 0xFF2A2420);
        int energyColor = HorseStats.hasFlag(horse, HorseByte.FLAG_STAMINA_POTION) ? 0xFFFFC940 : winded ? 0xFFB0B0B0 : 0xFF5DB0E8;
        graphics.fill(x + 4, y + 21, x + 4 + Math.round(energyW * energy), y + 23, energyColor);
        if (HorseStats.hasFlag(horse, HorseByte.FLAG_LAME)) {
            Component lame = Component.translatable("advancedhorses.status.lame");
            graphics.text(font, lame, x + WIDTH - 4 - font.width(lame), y + 3, 0xFFE57373);
        }
        graphics.text(font, Component.translatable(gait.translationKey()), x + 4, y + 3, 0xFFFFFFFF);

        int pips = HorseGait.values().length - 1;
        int pipW = (WIDTH - 8 - (pips - 1) * 2) / pips;
        for (int i = 1; i <= pips; i++) {
            int px = x + 4 + (i - 1) * (pipW + 2);
            int color = i <= gait.ordinal() ? PIP_ON : i > max.ordinal() ? PIP_BLOCKED : PIP_OFF;
            graphics.fill(px, y + 13, px + pipW, y + 16, color);
        }

        if (horse.isTamed()) {
            float hunger = HorseStats.get(horse, HorseStat.HUNGER) / HorseStats.MAX_HUNGER;
            int barW = WIDTH - 8;
            graphics.fill(x + 4, y + 18, x + 4 + barW, y + 20, 0xFF2A2420);
            graphics.fill(x + 4, y + 18, x + 4 + Math.round(barW * hunger), y + 20, hunger < 0.25F ? 0xFFD04040 : 0xFF7CB342);
        }
    }
}
