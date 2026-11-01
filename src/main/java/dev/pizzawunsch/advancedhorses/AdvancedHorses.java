package dev.pizzawunsch.advancedhorses;

import dev.pizzawunsch.advancedhorses.book.HorseRegistry;
import dev.pizzawunsch.advancedhorses.network.ModNetworking;
import dev.pizzawunsch.advancedhorses.race.RaceCommand;
import dev.pizzawunsch.advancedhorses.race.RaceManager;
import dev.pizzawunsch.advancedhorses.registry.ModBlocks;
import dev.pizzawunsch.advancedhorses.registry.ModItems;
import dev.pizzawunsch.advancedhorses.registry.ModRules;
import dev.pizzawunsch.advancedhorses.taming.TamingInteractions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

public class AdvancedHorses implements ModInitializer {
    public static final String MOD_ID = "advancedhorses";

    @Override
    public void onInitialize() {
        ModRules.init();
        ModBlocks.init();
        ModItems.init();
        ModNetworking.register();

        UseEntityCallback.EVENT.register(TamingInteractions::interact);
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> HorseRegistry.onDeath(entity));
        CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) -> RaceCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            HorseRegistry.load(server);
            RaceManager.load(server);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            HorseRegistry.save(server, true);
            RaceManager.save(server, true);
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            RaceManager.tick(server);
            if (server.getTickCount() % 1200 == 0) {
                HorseRegistry.save(server, false);
            }
        });
    }
}
