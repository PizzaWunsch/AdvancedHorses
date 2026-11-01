package dev.pizzawunsch.advancedhorses.client.gait;

import dev.pizzawunsch.advancedhorses.horse.HorseGait;
import dev.pizzawunsch.advancedhorses.network.GaitPayload;
import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public final class GaitController {
    private static boolean wasForward;
    private static boolean wasBack;

    private GaitController() {
    }

    public static void tick(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null || !(player.getVehicle() instanceof AbstractHorse horse) || horse.getControllingPassenger() != player
            || !TamingManager.isTameableType(horse)) {
            HorseGait.clientControlledId = -1;
            HorseGait.clientGear = 0;
            wasForward = false;
            wasBack = false;
            return;
        }

        if (HorseGait.clientControlledId != horse.getId()) {
            HorseGait.clientControlledId = horse.getId();
            HorseGait.clientGear = 0;
            send(0);
        }

        boolean forward = minecraft.options.keyUp.isDown();
        boolean back = minecraft.options.keyDown.isDown();
        int gear = HorseGait.clientGear;
        if (minecraft.gui.screen() == null) {
            if (forward && !wasForward) {
                gear++;
            }
            if (back && !wasBack && gear > 0) {
                gear--;
            }
        }
        gear = Math.clamp(gear, 0, HorseGait.maxAllowed(horse).ordinal());

        if (gear != HorseGait.clientGear) {
            HorseGait.clientGear = gear;
            send(gear);
        }
        wasForward = forward;
        wasBack = back;
    }

    private static void send(int gear) {
        if (ClientPlayNetworking.canSend(GaitPayload.TYPE)) {
            ClientPlayNetworking.send(new GaitPayload(gear));
        }
    }
}
