package dev.pizzawunsch.advancedhorses.client.screen;

import dev.pizzawunsch.advancedhorses.book.HorseEntry;
import dev.pizzawunsch.advancedhorses.horse.HorseBreed;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class HorseBookScreen extends Screen {
    private static final int WIDTH = 300;
    private static final int ROW = 34;
    private static final int BACKGROUND = 0xE0201A16;
    private static final int BORDER = 0xFF8B6B3D;
    private static final int TITLE = 0xFFFFD27F;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int HINT = 0xFFAAAAAA;
    private static final int ROW_BACKGROUND = 0x40FFFFFF;

    private final List<HorseEntry> entries;
    private double scroll;

    public HorseBookScreen(List<HorseEntry> entries) {
        super(Component.translatable("advancedhorses.book.title"));
        this.entries = entries;
    }

    private int top() {
        return 30;
    }

    private int listHeight() {
        return this.height - this.top() - 40;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
            .bounds(this.width / 2 - 50, this.height - 30, 100, 20)
            .build());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int max = Math.max(0, this.entries.size() * ROW - this.listHeight());
        this.scroll = Mth.clamp(this.scroll - scrollY * 20.0, 0.0, max);
        return true;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int left = this.width / 2 - WIDTH / 2;
        int top = this.top();
        int height = this.listHeight();
        graphics.fill(left - 4, top - 4, left + WIDTH + 4, top + height + 4, BACKGROUND);
        graphics.outline(left - 4, top - 4, WIDTH + 8, height + 8, BORDER);
        graphics.centeredText(this.font, this.title, this.width / 2, 12, TITLE);

        if (this.entries.isEmpty()) {
            graphics.centeredText(this.font, Component.translatable("advancedhorses.book.empty"), this.width / 2, top + 10, HINT);
        }

        graphics.enableScissor(left, top, left + WIDTH, top + height);
        int y = top - (int) this.scroll;
        for (HorseEntry entry : this.entries) {
            if (y + ROW >= top && y <= top + height) {
                this.drawEntry(graphics, entry, left, y);
            }
            y += ROW;
        }
        graphics.disableScissor();
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    private void drawEntry(GuiGraphicsExtractor graphics, HorseEntry entry, int x, int y) {
        graphics.fill(x, y + 1, x + WIDTH, y + ROW - 2, ROW_BACKGROUND);
        HorseBreed breed = HorseBreed.byId(entry.breed());
        Component title = Component.literal(entry.name()).append(Component.literal("  "))
            .append(Component.translatable(breed.translationKey()).withColor(HINT));
        graphics.text(this.font, title, x + 6, y + 4, TEXT);

        String stats = String.format(Locale.ROOT, "%.1f m/s  ·  %s %d  ·  %s %d  ·  %s %d%%",
            entry.speed(),
            Component.translatable("advancedhorses.stat.stamina").getString(), entry.stamina(),
            Component.translatable("advancedhorses.stat.strength").getString(), entry.strength(),
            Component.translatable("advancedhorses.stat.bond").getString(), entry.bond());
        graphics.text(this.font, stats, x + 6, y + 14, HINT);

        String dimension = entry.dimension().contains(":") ? entry.dimension().substring(entry.dimension().indexOf(':') + 1) : entry.dimension();
        Component location = Component.translatable("advancedhorses.book.location", entry.x(), entry.y(), entry.z(), dimension);
        graphics.text(this.font, location, x + 6, y + 23, HINT);
        String hunger = entry.hunger() + "%";
        graphics.text(this.font, hunger, x + WIDTH - 6 - this.font.width(hunger), y + 4, entry.hunger() < 25 ? 0xFFD04040 : 0xFF7CB342);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
