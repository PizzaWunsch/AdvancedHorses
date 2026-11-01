package dev.pizzawunsch.advancedhorses.client;

import dev.pizzawunsch.advancedhorses.AdvancedHorses;
import dev.pizzawunsch.advancedhorses.client.gait.GaitController;
import dev.pizzawunsch.advancedhorses.client.gait.GaitHud;
import dev.pizzawunsch.advancedhorses.client.hud.TamingHud;
import dev.pizzawunsch.advancedhorses.client.screen.HorseBookScreen;
import dev.pizzawunsch.advancedhorses.client.screen.HorseNameScreen;
import dev.pizzawunsch.advancedhorses.network.HorseBookPayload;
import dev.pizzawunsch.advancedhorses.network.NamePromptPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class AdvancedHorsesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CROSSHAIR,
            Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, "taming"), new TamingHud());
        HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR,
            Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, "gait"), new GaitHud());
        ClientTickEvents.END_CLIENT_TICK.register(GaitController::tick);
        ClientPlayNetworking.registerGlobalReceiver(NamePromptPayload.TYPE,
            (payload, context) -> context.client().gui.setScreen(new HorseNameScreen(payload.entityId())));
        ClientPlayNetworking.registerGlobalReceiver(HorseBookPayload.TYPE,
            (payload, context) -> context.client().gui.setScreen(new HorseBookScreen(payload.entries())));
    }
}
