package dev.pizzawunsch.advancedhorses.client.hud;

import dev.pizzawunsch.advancedhorses.horse.HorseBreed;
import dev.pizzawunsch.advancedhorses.horse.HorseByte;
import dev.pizzawunsch.advancedhorses.horse.HorseGait;
import dev.pizzawunsch.advancedhorses.horse.HorseLevel;
import dev.pizzawunsch.advancedhorses.horse.HorseRest;
import dev.pizzawunsch.advancedhorses.horse.HorseStat;
import dev.pizzawunsch.advancedhorses.horse.HorseStats;
import dev.pizzawunsch.advancedhorses.horse.Horseshoe;
import dev.pizzawunsch.advancedhorses.horse.Pedigree;
import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Horse;

public final class HorseStatsPanel {
    public static final int WIDTH = 140;
    public static final int PEDIGREE_WIDTH = 140;
    private static final int ROW = 17;
    private static final int LINE = 10;

    private static final int BACKGROUND = 0xE0201A16;
    private static final int BORDER = 0xFF8B6B3D;
    private static final int TITLE = 0xFFFFD27F;
    private static final int LABEL = 0xFFCCCCCC;
    private static final int VALUE = 0xFFFFFFFF;
    private static final int HINT = 0xFF999999;
    private static final int WARNING = 0xFFE57373;
    private static final int BAR_BACK = 0xFF2A2420;
    private static final int LEVELED = 0xFFB3E5FC;

    private HorseStatsPanel() {
    }

    private static int bars(AbstractHorse horse) {
        return horse.isTamed() ? 8 : 6;
    }

    public static int height(AbstractHorse horse) {
        return 16 + 2 * LINE + 3 + bars(horse) * ROW + 14;
    }

    public static void draw(GuiGraphicsExtractor graphics, Font font, AbstractHorse horse, int x, int y) {
        draw(graphics, font, horse, x, y, Component.translatable("advancedhorses.stats.title"));
    }

    public static void draw(GuiGraphicsExtractor graphics, Font font, AbstractHorse horse, int x, int y, Component title) {
        boolean tamed = horse.isTamed();
        int height = height(horse);
        graphics.fill(x, y, x + WIDTH, y + height, BACKGROUND);
        graphics.outline(x, y, WIDTH, height, BORDER);
        graphics.centeredText(font, title, x + WIDTH / 2, y + 5, TITLE);

        int lineY = y + 17;
        HorseBreed breed = HorseStats.breed(horse);
        Component identity = breed == HorseBreed.DONKEY
            ? horse.getType().getDescription()
            : Component.translatable(breed.translationKey());
        graphics.text(font, Component.empty().append(identity).append(" · ").append(Component.translatable(HorseStats.trait(horse).translationKey())),
            x + 6, lineY, LABEL);
        lineY += LINE;
        Component age = Component.translatable("advancedhorses.stats.age", HorseStats.ageYears(horse));
        Horseshoe shoes = HorseStats.shoes(horse);
        graphics.text(font, Component.empty().append(age).append(" · ").append(Component.translatable(shoes.translationKey())),
            x + 6, lineY, LABEL);

        int rowY = lineY + LINE + 3;
        if (tamed) {
            int level = HorseLevel.level(horse);
            float needed = HorseLevel.xpForNext(level);
            float xp = HorseStats.get(horse, HorseStat.XP);
            String progress = level >= HorseLevel.MAX_LEVEL
                ? Component.translatable("advancedhorses.stat.level.max").getString()
                : Math.round(xp) + "/" + Math.round(needed) + " " + Component.translatable("advancedhorses.stat.xp").getString();
            graphics.text(font, Component.translatable("advancedhorses.stat.level", level), x + 6, rowY, TITLE);
            graphics.text(font, progress, x + WIDTH - 6 - font.width(progress), rowY, VALUE);
            bar(graphics, x, rowY, level >= HorseLevel.MAX_LEVEL ? 1.0F : xp / needed, 0.0F, 0xFFFFD27F);
            rowY += ROW;
        }

        double baseSpeed = horse.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * breed.speed();
        double speed = HorseStats.speed(horse);
        float speedRating = rating(baseSpeed, HorseStats.MIN_SPEED, HorseStats.MAX_SPEED);
        float speedBonus = rating(speed, HorseStats.MIN_SPEED, HorseStats.MAX_SPEED) - speedRating;
        row(graphics, font, x, rowY, "advancedhorses.stat.speed",
            String.format(Locale.ROOT, "%.1f m/s", HorseStats.blocksPerSecond(speed)), speedRating, speedBonus, 0xFFE0A030);
        rowY += ROW;

        double baseJump = horse.getAttributeBaseValue(Attributes.JUMP_STRENGTH);
        double jump = horse.getAttributeValue(Attributes.JUMP_STRENGTH);
        float jumpRating = rating(baseJump, 0.4, 1.0);
        row(graphics, font, x, rowY, "advancedhorses.stat.jump",
            String.format(Locale.ROOT, "%.1f", HorseStats.jumpHeight(jump)), jumpRating, rating(jump, 0.4, 1.0) - jumpRating, 0xFF81C784);
        rowY += ROW;

        float health = horse.getHealth();
        float maxHealth = horse.getMaxHealth();
        row(graphics, font, x, rowY, "advancedhorses.stat.health",
            Mth.ceil(health) + "/" + Mth.ceil(maxHealth), health / maxHealth, 0.0F, 0xFFE57373);
        rowY += ROW;

        float staminaBase = HorseStats.baseStamina(horse);
        float stamina = HorseStats.stamina(horse);
        row(graphics, font, x, rowY, "advancedhorses.stat.stamina", withBonus(stamina, staminaBase), staminaBase, stamina - staminaBase, 0xFF64B5F6);
        rowY += ROW;

        float strengthBase = HorseStats.baseStrength(horse);
        float strength = HorseStats.strength(horse);
        String strengthText = withBonus(strength, strengthBase);
        if (horse instanceof Horse) {
            strengthText += "  " + HorseStats.unlockedSlots(horse) + "/" + HorseStats.MAX_SLOTS;
        }
        row(graphics, font, x, rowY, "advancedhorses.stat.strength", strengthText, strengthBase, strength - strengthBase, 0xFFBA68C8);
        rowY += ROW;

        float bond = HorseStats.bond(horse);
        row(graphics, font, x, rowY, "advancedhorses.stat.bond", Math.round(bond * 100.0F) + "%", bond, 0.0F, 0xFFF06292);
        rowY += ROW;

        if (tamed) {
            float hunger = HorseStats.get(horse, HorseStat.HUNGER);
            row(graphics, font, x, rowY, "advancedhorses.stat.hunger",
                Math.round(hunger) + "%", hunger / HorseStats.MAX_HUNGER, 0.0F, hunger < 25.0F ? 0xFFD04040 : 0xFF7CB342);
            rowY += ROW;
        }

        Component status = status(horse);
        boolean bad = HorseStats.hasFlag(horse, HorseByte.FLAG_LAME) || TamingManager.access(horse).advancedhorses$getRest() == HorseRest.EXHAUSTED;
        graphics.text(font, status, x + 6, rowY + 1, bad ? WARNING : HINT);
    }

    private static Component status(AbstractHorse horse) {
        int rest = TamingManager.access(horse).advancedhorses$getRest();
        if (rest == HorseRest.EXHAUSTED) {
            return Component.translatable("advancedhorses.status.exhausted");
        } else if (HorseStats.hasFlag(horse, HorseByte.FLAG_LAME)) {
            return Component.translatable("advancedhorses.status.lame");
        } else if (rest == HorseRest.LYING) {
            return Component.translatable("advancedhorses.status.resting");
        } else if (!horse.isTamed()) {
            return Component.translatable("advancedhorses.status.wild");
        } else if (HorseStats.hasFlag(horse, HorseByte.FLAG_WINDED)) {
            return Component.translatable("advancedhorses.status.winded");
        }
        return Component.translatable("advancedhorses.status.max_gait", Component.translatable(HorseGait.maxAllowed(horse).translationKey()));
    }

    public static int pedigreeHeight() {
        return 16 + 2 * 22 + 4 * LINE + 10;
    }

    public static void drawPedigree(GuiGraphicsExtractor graphics, Font font, AbstractHorse horse, int x, int y) {
        Map<String, String> pedigree = Pedigree.parse(TamingManager.access(horse).advancedhorses$getPedigree());
        int height = pedigree.isEmpty() ? 34 : pedigreeHeight();
        graphics.fill(x, y, x + PEDIGREE_WIDTH, y + height, BACKGROUND);
        graphics.outline(x, y, PEDIGREE_WIDTH, height, BORDER);
        graphics.centeredText(font, Component.translatable("advancedhorses.pedigree.title"), x + PEDIGREE_WIDTH / 2, y + 5, TITLE);
        if (pedigree.isEmpty()) {
            graphics.text(font, Component.translatable("advancedhorses.pedigree.wild"), x + 6, y + 19, HINT);
            return;
        }

        int rowY = y + 18;
        rowY = parent(graphics, font, x, rowY, "advancedhorses.pedigree.mother", pedigree.get(Pedigree.MOTHER));
        rowY = parent(graphics, font, x, rowY, "advancedhorses.pedigree.father", pedigree.get(Pedigree.FATHER));
        String[] keys = {"advancedhorses.pedigree.mm", "advancedhorses.pedigree.mf", "advancedhorses.pedigree.fm", "advancedhorses.pedigree.ff"};
        for (int i = 0; i < Pedigree.GRANDPARENTS.length; i++) {
            String name = pedigree.get(Pedigree.GRANDPARENTS[i]);
            Component line = Component.translatable(keys[i]).append(": ").append(name == null ? "?" : name);
            graphics.text(font, line, x + 6, rowY, HINT);
            rowY += LINE;
        }
    }

    private static int parent(GuiGraphicsExtractor graphics, Font font, int x, int y, String label, String entry) {
        if (entry == null) {
            graphics.text(font, Component.translatable(label).append(": ?"), x + 6, y, LABEL);
            return y + 22;
        }
        graphics.text(font, Component.translatable(label).append(": ").append(Pedigree.name(entry)), x + 6, y, VALUE);
        String[] stats = Pedigree.stats(entry);
        if (stats != null) {
            HorseBreed breed;
            try {
                breed = HorseBreed.byId(Integer.parseInt(stats[3]));
            } catch (NumberFormatException exception) {
                breed = HorseBreed.UNKNOWN;
            }
            String values = stats[0] + " m/s · " + stats[1] + " · " + stats[2];
            graphics.text(font, Component.translatable(breed.translationKey()).append(" · " + values), x + 6, y + LINE, HINT);
        }
        return y + 22;
    }

    private static String withBonus(float value, float base) {
        int total = Math.round(value * 100.0F);
        int bonus = total - Math.round(base * 100.0F);
        return bonus > 0 ? total + " (+" + bonus + ")" : String.valueOf(total);
    }

    private static void row(GuiGraphicsExtractor graphics, Font font, int x, int y, String label, String value,
                            float base, float bonus, int color) {
        graphics.text(font, Component.translatable(label), x + 6, y, LABEL);
        graphics.text(font, value, x + WIDTH - 6 - font.width(value), y, VALUE);
        bar(graphics, x, y, base, bonus, color);
    }

    private static void bar(GuiGraphicsExtractor graphics, int x, int y, float base, float bonus, int color) {
        int barX = x + 6;
        int barY = y + 10;
        int barW = WIDTH - 12;
        graphics.fill(barX, barY, barX + barW, barY + 3, BAR_BACK);
        int baseW = Math.round(barW * Mth.clamp(base, 0.0F, 1.0F));
        int bonusW = Math.round(barW * Mth.clamp(base + bonus, 0.0F, 1.0F)) - baseW;
        if (baseW > 0) {
            graphics.fill(barX, barY, barX + baseW, barY + 3, color);
        }
        if (bonusW > 0) {
            graphics.fill(barX + baseW, barY, barX + baseW + bonusW, barY + 3, LEVELED);
        }
    }

    private static float rating(double value, double min, double max) {
        return (float) Mth.clamp((value - min) / (max - min), 0.0, 1.0);
    }

    public static void drawLockedSlot(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x, y, x + 16, y + 16, 0xB0101010);
        int cx = x + 8;
        graphics.fill(cx - 3, y + 3, cx + 3, y + 4, 0xFFAAAAAA);
        graphics.fill(cx - 3, y + 3, cx - 2, y + 8, 0xFFAAAAAA);
        graphics.fill(cx + 2, y + 3, cx + 3, y + 8, 0xFFAAAAAA);
        graphics.fill(cx - 4, y + 7, cx + 4, y + 13, 0xFFD0A040);
        graphics.fill(cx - 1, y + 9, cx + 1, y + 11, 0xFF5A4010);
    }
}
