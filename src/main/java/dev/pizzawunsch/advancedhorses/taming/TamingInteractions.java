package dev.pizzawunsch.advancedhorses.taming;

import dev.pizzawunsch.advancedhorses.anim.HorseAnimation;
import dev.pizzawunsch.advancedhorses.horse.HorseLife;
import dev.pizzawunsch.advancedhorses.horse.HorseRest;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public final class TamingInteractions {
    private TamingInteractions() {
    }

    public static InteractionResult interact(Player player, Level level, InteractionHand hand, Entity entity, EntityHitResult hitResult) {
        if (!(entity instanceof AbstractHorse horse) || player.isSpectator() || horse.isVehicle()) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);
        if (horse.isTamed() && TamingManager.isTameableType(horse)) {
            if (stack.is(Items.BRUSH)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    horse.level().playSound(null, horse.getX(), horse.getY(), horse.getZ(), SoundEvents.ARMADILLO_BRUSH, SoundSource.NEUTRAL, 1.0F, 1.1F);
                    if (HorseLife.groom(horse, serverPlayer)) {
                        stack.hurtAndBreak(1, player, hand);
                        TamingManager.playAnimation(horse, HorseAnimation.NUZZLE);
                    } else {
                        serverPlayer.sendOverlayMessage(Component.translatable("advancedhorses.groomed").withStyle(ChatFormatting.GRAY));
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (horse.isTamed() && TamingManager.isTameableType(horse) && !horse.isBaby()
            && TamingManager.access(horse).advancedhorses$getRest() == HorseRest.EXHAUSTED
            && !player.isSecondaryUseActive() && !stack.is(ItemTags.HORSE_FOOD)) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendOverlayMessage(Component.translatable("advancedhorses.exhausted").withStyle(ChatFormatting.RED));
            }
            return InteractionResult.SUCCESS;
        }

        if (!TamingManager.isBeingTamed(horse)) {
            return InteractionResult.PASS;
        }
        TamingManager.Action action;
        if (stack.is(ItemTags.HORSE_FOOD)) {
            action = TamingManager.Action.FEED;
        } else if (stack.is(Items.BRUSH)) {
            action = TamingManager.Action.GROOM;
        } else if (stack.isEmpty() && hand == InteractionHand.MAIN_HAND && player.isShiftKeyDown()) {
            action = TamingManager.Action.PET;
        } else {
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            return TamingManager.handleInteraction(horse, serverPlayer, hand, action);
        }
        return InteractionResult.SUCCESS;
    }
}
