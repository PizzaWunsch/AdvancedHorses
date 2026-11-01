package dev.pizzawunsch.advancedhorses.client.screen;

import dev.pizzawunsch.advancedhorses.network.RenameHorsePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class HorseNameScreen extends Screen {
    private static final int BOX_WIDTH = 200;

    private final int entityId;
    private EditBox nameBox;
    private Button doneButton;

    public HorseNameScreen(int entityId) {
        super(Component.translatable("advancedhorses.name.title"));
        this.entityId = entityId;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - BOX_WIDTH / 2;
        int y = this.height / 2 - 10;

        this.nameBox = new EditBox(this.font, x, y, BOX_WIDTH, 20, Component.translatable("advancedhorses.name.title"));
        this.nameBox.setMaxLength(RenameHorsePayload.MAX_LENGTH);
        this.nameBox.setHint(Component.translatable("advancedhorses.name.hint"));
        this.nameBox.setResponder(value -> this.updateButton());
        Entity horse = this.minecraft.level == null ? null : this.minecraft.level.getEntity(this.entityId);
        if (horse != null && horse.hasCustomName()) {
            this.nameBox.setValue(horse.getCustomName().getString());
        }
        this.addRenderableWidget(this.nameBox);

        this.doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.confirm())
            .bounds(x, y + 28, BOX_WIDTH / 2 - 2, 20)
            .build());
        this.addRenderableWidget(Button.builder(Component.translatable("advancedhorses.name.skip"), button -> this.onClose())
            .bounds(x + BOX_WIDTH / 2 + 2, y + 28, BOX_WIDTH / 2 - 2, 20)
            .build());
        this.setInitialFocus(this.nameBox);
        this.updateButton();
    }

    private void updateButton() {
        this.doneButton.active = !this.nameBox.getValue().isBlank();
    }

    private void confirm() {
        String name = this.nameBox.getValue().strip();
        if (!name.isEmpty() && ClientPlayNetworking.canSend(RenameHorsePayload.TYPE)) {
            ClientPlayNetworking.send(new RenameHorsePayload(this.entityId, name));
        }
        this.onClose();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isConfirmation() && this.nameBox.isFocused() && !this.nameBox.getValue().isBlank()) {
            this.confirm();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(this.font, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFD27F);
        graphics.centeredText(this.font, Component.translatable("advancedhorses.name.subtitle"), this.width / 2, this.height / 2 - 26, 0xFFCCCCCC);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
