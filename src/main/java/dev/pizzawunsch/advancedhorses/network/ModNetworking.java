package dev.pizzawunsch.advancedhorses.network;

import dev.pizzawunsch.advancedhorses.book.HorseRegistry;
import dev.pizzawunsch.advancedhorses.horse.HorseGait;
import dev.pizzawunsch.advancedhorses.horse.HorseLife;
import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public final class ModNetworking {
    private static final double RENAME_RANGE = 16.0;

    private ModNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(GaitPayload.TYPE, GaitPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(RenameHorsePayload.TYPE, RenameHorsePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(NamePromptPayload.TYPE, NamePromptPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(HorseBookPayload.TYPE, HorseBookPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(GaitPayload.TYPE, (payload, context) -> handleGait(context.player(), payload));
        ServerPlayNetworking.registerGlobalReceiver(RenameHorsePayload.TYPE, (payload, context) -> handleRename(context.player(), payload));
    }

    public static void sendHorseBook(ServerPlayer player) {
        if (!ServerPlayNetworking.canSend(player, HorseBookPayload.TYPE)) {
            return;
        }
        for (AbstractHorse horse : player.level().getEntitiesOfClass(AbstractHorse.class, player.getBoundingBox().inflate(128.0),
            horse -> horse.isTamed() && HorseLife.isOwner(horse, player))) {
            HorseRegistry.update(horse);
        }
        ServerPlayNetworking.send(player, new HorseBookPayload(HorseRegistry.entries(player.getUUID())));
    }

    public static void sendNamePrompt(ServerPlayer player, AbstractHorse horse) {
        if (ServerPlayNetworking.canSend(player, NamePromptPayload.TYPE)) {
            ServerPlayNetworking.send(player, new NamePromptPayload(horse.getId()));
        }
    }

    private static void handleGait(ServerPlayer player, GaitPayload payload) {
        if (!(player.getVehicle() instanceof AbstractHorse horse) || horse.getControllingPassenger() != player
            || !TamingManager.isTameableType(horse)) {
            return;
        }
        HorseGait from = HorseGait.byIndex(TamingManager.access(horse).advancedhorses$getGear());
        HorseGait to = HorseGait.byIndex(Math.min(payload.gear(), HorseGait.maxAllowed(horse).ordinal()));
        TamingManager.access(horse).advancedhorses$setGear(to.ordinal());
        HorseLife.onGaitChanged(horse, from, to);
    }

    private static void handleRename(ServerPlayer player, RenameHorsePayload payload) {
        if (!(player.level().getEntity(payload.entityId()) instanceof AbstractHorse horse) || !horse.isTamed()
            || !HorseLife.isOwner(horse, player) || horse.distanceToSqr(player) > RENAME_RANGE * RENAME_RANGE) {
            return;
        }

        String name = payload.name().replace("\u00a7", "").strip();
        if (name.isEmpty()) {
            return;
        }
        if (name.length() > RenameHorsePayload.MAX_LENGTH) {
            name = name.substring(0, RenameHorsePayload.MAX_LENGTH);
        }
        horse.setCustomName(Component.literal(name));
        player.sendOverlayMessage(Component.translatable("advancedhorses.named", name).withStyle(ChatFormatting.GREEN));
        HorseRegistry.update(horse);
    }
}
