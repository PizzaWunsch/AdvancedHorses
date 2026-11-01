package dev.pizzawunsch.advancedhorses.client.hud;

import dev.pizzawunsch.advancedhorses.taming.TamingData;
import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import dev.pizzawunsch.advancedhorses.taming.TamingNeed;
import dev.pizzawunsch.advancedhorses.taming.TamingTreat;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;

public final class TamingHud implements HudElement {
    private static final double RANGE = 16.0;
    private static final int WIDTH = 156;
    private static final int HEADER = 28;
    private static final int ROW = 21;
    private static final int FOOTER = 12;
    private static final int HEIGHT = HEADER + ROW * TamingData.SLOTS + FOOTER;

    private static final int BACKGROUND = 0xC8141010;
    private static final int BORDER = 0xFF8B6B3D;
    private static final int TITLE = 0xFFFFD27F;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int DONE = 0xFF7CE07C;
    private static final int HINT = 0xFFAAAAAA;
    private static final int BAR_BACK = 0xFF2A2420;
    private static final int BAR_TOTAL = 0xFFE0A030;
    private static final int BAR_NEED = 0xFF5DB0E8;

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity viewer = minecraft.getCameraEntity();
        if (minecraft.level == null || viewer == null) {
            return;
        }

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        AbstractHorse horse = findTarget(viewer, partialTick);
        if (horse == null) {
            return;
        }

        Camera camera = minecraft.gameRenderer.mainCamera();
        Vec3 anchor = horse.getPosition(partialTick).add(0.0, horse.getBbHeight() + 0.4, 0.0);
        Vec3 rel = anchor.subtract(camera.position());
        Vector3fc forward = camera.forwardVector();
        Vector3fc up = camera.upVector();
        Vector3fc left = camera.leftVector();
        double depth = rel.x * forward.x() + rel.y * forward.y() + rel.z * forward.z();
        if (depth < 0.2) {
            return;
        }

        int screenW = graphics.guiWidth();
        int screenH = graphics.guiHeight();
        double focal = screenH / 2.0 / Math.tan(Math.toRadians(camera.getFov()) / 2.0);
        double sx = screenW / 2.0 - (rel.x * left.x() + rel.y * left.y() + rel.z * left.z()) / depth * focal;
        double sy = screenH / 2.0 - (rel.x * up.x() + rel.y * up.y() + rel.z * up.z()) / depth * focal;

        boolean taming = TamingManager.isBeingTamed(horse);
        int panelW = taming ? WIDTH : HorseStatsPanel.WIDTH;
        int panelH = taming ? HEIGHT : HorseStatsPanel.height(horse);
        float scale = Mth.clamp(1.1F - (float) rel.length() / 28.0F, 0.6F, 1.0F);
        float halfW = panelW * scale / 2.0F;
        float x = Mth.clamp((float) sx, halfW + 4.0F, screenW - halfW - 4.0F);
        float y = Mth.clamp((float) sy, panelH * scale + 4.0F, screenH - 4.0F);

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(scale, scale);
        if (taming) {
            drawPanel(graphics, minecraft.font, horse, -WIDTH / 2, -HEIGHT);
        } else {
            HorseStatsPanel.draw(graphics, minecraft.font, horse, -panelW / 2, -panelH, horse.getName());
        }
        graphics.pose().popMatrix();
    }

    private static AbstractHorse findTarget(Entity viewer, float partialTick) {
        if (viewer.isPassenger() && viewer.getVehicle() instanceof AbstractHorse) {
            return null;
        }

        Vec3 eye = viewer.getEyePosition(partialTick);
        Vec3 look = viewer.getViewVector(partialTick).scale(RANGE);
        HitResult block = viewer.pick(RANGE, partialTick, false);
        double maxDistSqr = block.getType() == HitResult.Type.MISS ? RANGE * RANGE : block.getLocation().distanceToSqr(eye);
        AABB area = viewer.getBoundingBox().expandTowards(look).inflate(1.0);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(viewer, eye, eye.add(look), area,
            entity -> entity instanceof AbstractHorse horse && isInspectable(viewer, horse), maxDistSqr);
        return hit != null && hit.getEntity() instanceof AbstractHorse horse ? horse : null;
    }

    private static boolean isInspectable(Entity viewer, AbstractHorse horse) {
        if (TamingManager.isBeingTamed(horse)) {
            return true;
        }
        return horse.isTamed() && TamingManager.isTameableType(horse) && viewer.isCrouching();
    }

    private static void drawPanel(GuiGraphicsExtractor graphics, Font font, AbstractHorse horse, int x, int y) {
        int needs = TamingManager.access(horse).advancedhorses$getSyncedNeeds();
        int progress = TamingManager.access(horse).advancedhorses$getSyncedProgress();
        float total = TamingData.totalProgress(progress);

        graphics.fill(x, y, x + WIDTH, y + HEIGHT, BACKGROUND);
        graphics.outline(x, y, WIDTH, HEIGHT, BORDER);

        graphics.centeredText(font, Component.translatable("advancedhorses.hud.title", horse.getName()), x + WIDTH / 2, y + 4, TITLE);
        int barX = x + 6;
        int barY = y + 15;
        int barW = WIDTH - 12;
        bar(graphics, barX, barY, barW, 10, total, BAR_TOTAL);
        graphics.centeredText(font, Math.round(total * 100.0F) + "%", x + WIDTH / 2, barY + 1, TEXT);

        TamingNeed firstOpen = null;
        TamingTreat treat = TamingData.treat(needs);
        for (int slot = 0; slot < TamingData.SLOTS; slot++) {
            TamingNeed need = TamingData.need(needs, slot);
            if (need == null) {
                continue;
            }

            float value = TamingData.progress(progress, slot);
            boolean done = value >= 1.0F;
            if (!done && firstOpen == null) {
                firstOpen = need;
            }

            int rowY = y + HEADER + slot * ROW;
            ItemStack icon = need == TamingNeed.TREAT && treat != null ? treat.icon() : need.icon();
            graphics.fakeItem(icon, x + 5, rowY);

            Component name = Component.translatable(need.translationKey());
            if (need == TamingNeed.TREAT && treat != null) {
                name = Component.empty().append(name).append(": ").append(treat.icon().getHoverName());
            }
            graphics.text(font, name, x + 25, rowY + 1, done ? DONE : TEXT);
            Component amount = done ? Component.literal("✔") : Component.literal(Math.round(value * 100.0F) + "%");
            graphics.text(font, amount, x + WIDTH - 6 - font.width(amount), rowY + 1, done ? DONE : HINT);
            bar(graphics, x + 25, rowY + 12, WIDTH - 31, 4, value, done ? DONE : BAR_NEED);
        }

        if (firstOpen != null) {
            Component hint = Component.translatable(firstOpen.hintKey());
            graphics.pose().pushMatrix();
            graphics.pose().translate(x + WIDTH / 2.0F, y + HEIGHT - FOOTER + 2);
            float hintScale = Math.min(0.75F, (WIDTH - 8.0F) / Math.max(1, font.width(hint)));
            graphics.pose().scale(hintScale, hintScale);
            graphics.centeredText(font, hint, 0, 0, HINT);
            graphics.pose().popMatrix();
        }
    }

    private static void bar(GuiGraphicsExtractor graphics, int x, int y, int width, int height, float value, int color) {
        graphics.fill(x, y, x + width, y + height, BAR_BACK);
        int filled = Math.round(width * Mth.clamp(value, 0.0F, 1.0F));
        if (filled > 0) {
            graphics.fill(x, y, x + filled, y + height, color);

            graphics.fill(x, y, x + filled, y + Math.max(1, height / 3), 0x40FFFFFF);
        }
    }
}
