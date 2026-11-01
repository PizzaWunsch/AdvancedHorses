package dev.pizzawunsch.advancedhorses.item;

import dev.pizzawunsch.advancedhorses.network.ModNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class HorseBookItem extends Item {
    public HorseBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            ModNetworking.sendHorseBook(serverPlayer);
        } else {
            player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }
}
