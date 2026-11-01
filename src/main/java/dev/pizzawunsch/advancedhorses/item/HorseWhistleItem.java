package dev.pizzawunsch.advancedhorses.item;

import dev.pizzawunsch.advancedhorses.horse.HorseLife;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class HorseWhistleItem extends Item {
    public static final int COOLDOWN = 60;

    public HorseWhistleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            HorseLife.whistle(serverPlayer);
        }
        player.getCooldowns().addCooldown(player.getItemInHand(hand), COOLDOWN);
        return InteractionResult.SUCCESS;
    }
}
