package dev.pizzawunsch.advancedhorses.taming;

import dev.pizzawunsch.advancedhorses.access.AdvancedHorseAccess;
import dev.pizzawunsch.advancedhorses.anim.HorseAnimation;
import dev.pizzawunsch.advancedhorses.horse.HorseRest;
import dev.pizzawunsch.advancedhorses.horse.HorseStats;
import dev.pizzawunsch.advancedhorses.mixin.AbstractHorseInvoker;
import dev.pizzawunsch.advancedhorses.network.ModNetworking;
import dev.pizzawunsch.advancedhorses.registry.ModRules;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Donkey;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;

public final class TamingManager {
    private static final long DECAY_DELAY = 2400L;
    private static final int DECAY_INTERVAL = 100;
    private static final float DECAY_FRACTION = 0.03F;
    private static final float HURT_PENALTY = 0.25F;
    private static final double COMPANY_RANGE = 6.0;
    private static final double NERVOUS_RANGE = 8.0;

    public enum Action {
        FEED,
        PET,
        GROOM
    }

    private TamingManager() {
    }

    public static AdvancedHorseAccess access(AbstractHorse horse) {
        return (AdvancedHorseAccess) horse;
    }

    public static boolean isTameableType(AbstractHorse horse) {
        return horse instanceof Horse || horse instanceof Donkey || horse instanceof Mule;
    }

    public static boolean isBeingTamed(AbstractHorse horse) {
        return isTameableType(horse) && !horse.isTamed() && !horse.isBaby() && access(horse).advancedhorses$getSyncedNeeds() != 0;
    }

    public static void serverTick(AbstractHorse horse, ServerLevel level) {
        if (!isTameableType(horse) || !horse.isAlive()) {
            return;
        }

        AdvancedHorseAccess access = access(horse);
        TamingState state = access.advancedhorses$getTamingState();
        HorseStats.ensureGenetics(horse);
        tickIdleAnimations(horse, level, state);

        int needs = access.advancedhorses$getSyncedNeeds();
        if (horse.isTamed() || horse.isBaby()) {
            if (needs != 0) {
                access.advancedhorses$setSyncedNeeds(0);
                access.advancedhorses$setSyncedProgress(0);
                state.reset();
            }
            return;
        }

        if (needs == 0) {
            initNeeds(horse, access, state);
            return;
        }

        long now = level.getGameTime();
        if (horse.tickCount % 20 == 0) {
            tickCompany(horse, level, needs, state);
        }

        if (horse.tickCount % DECAY_INTERVAL == 0 && state.lastProgressTime > 0L && now - state.lastProgressTime > DECAY_DELAY) {
            for (int slot = 0; slot < TamingData.SLOTS; slot++) {
                if (!isFulfilled(horse, needs, state, slot)) {
                    state.points[slot] = Math.max(0.0F, state.points[slot] - required(horse, needs, slot) * DECAY_FRACTION);
                }
            }
            sync(horse, needs, state);
        }
    }

    private static void initNeeds(AbstractHorse horse, AdvancedHorseAccess access, TamingState state) {
        RandomSource random = horse.getRandom();
        List<TamingNeed> pool = new ArrayList<>(List.of(TamingNeed.TREAT, TamingNeed.AFFECTION, TamingNeed.GROOMING, TamingNeed.TRUST, TamingNeed.COMPANY));
        TamingNeed[] needs = {
            TamingNeed.HUNGER,
            pool.remove(random.nextInt(pool.size())),
            pool.remove(random.nextInt(pool.size()))
        };
        TamingTreat treat = TamingTreat.values()[random.nextInt(TamingTreat.values().length)];

        state.reset();
        int packed = TamingData.packNeeds(needs, treat);
        access.advancedhorses$setSyncedNeeds(packed);
        sync(horse, packed, state);
    }

    public static InteractionResult handleInteraction(AbstractHorse horse, ServerPlayer player, InteractionHand hand, Action action) {
        AdvancedHorseAccess access = access(horse);
        TamingState state = access.advancedhorses$getTamingState();
        int needs = access.advancedhorses$getSyncedNeeds();
        long now = horse.level().getGameTime();
        ItemStack stack = player.getItemInHand(hand);

        switch (action) {
            case FEED -> {
                if (now < state.feedCooldownUntil) {
                    return InteractionResult.SUCCESS_SERVER;
                }

                TamingTreat treat = TamingData.treat(needs);
                boolean favorite = treat != null && stack.is(treat.item());
                int hungerSlot = slotOf(needs, TamingNeed.HUNGER);
                int treatSlot = favorite ? slotOf(needs, TamingNeed.TREAT) : -1;
                boolean feedsHunger = hungerSlot >= 0 && !isFulfilled(horse, needs, state, hungerSlot);
                boolean feedsTreat = treatSlot >= 0 && !isFulfilled(horse, needs, state, treatSlot);
                if (!feedsHunger && !feedsTreat) {
                    playAnimation(horse, HorseAnimation.HEAD_SHAKE);
                    player.sendOverlayMessage(Component.translatable("advancedhorses.feed.full").withStyle(ChatFormatting.GRAY));
                    return InteractionResult.SUCCESS_SERVER;
                }

                state.feedCooldownUntil = now + 10L;
                stack.consume(1, player);
                ((AbstractHorseInvoker) horse).advancedhorses$invokeEating();
                horse.gameEvent(GameEvent.EAT);
                spawnParticles(horse, favorite ? ParticleTypes.HEART : ParticleTypes.HAPPY_VILLAGER, favorite ? 3 : 5);
                if (feedsTreat) {
                    addPoints(horse, needs, state, treatSlot, 1.0F, now);
                }
                if (feedsHunger) {
                    addPoints(horse, needs, state, hungerSlot, foodValue(stack), now);
                }
            }
            case PET -> {
                if (now < state.petCooldownUntil) {
                    return InteractionResult.SUCCESS_SERVER;
                }

                state.petCooldownUntil = now + 40L;
                playAnimation(horse, HorseAnimation.NUZZLE);
                horse.playSound(SoundEvents.HORSE_BREATHE, 0.8F, 0.9F + horse.getRandom().nextFloat() * 0.2F);
                int slot = slotOf(needs, TamingNeed.AFFECTION);
                if (!checkContributes(horse, player, needs, state, slot)) {
                    return InteractionResult.SUCCESS_SERVER;
                }

                spawnParticles(horse, ParticleTypes.HEART, 2);
                addPoints(horse, needs, state, slot, 1.0F, now);
            }
            case GROOM -> {
                if (now < state.groomCooldownUntil) {
                    return InteractionResult.SUCCESS_SERVER;
                }

                state.groomCooldownUntil = now + 20L;
                horse.level().playSound(null, horse.getX(), horse.getY(), horse.getZ(), SoundEvents.ARMADILLO_BRUSH, SoundSource.NEUTRAL, 1.0F, 1.1F);
                int slot = slotOf(needs, TamingNeed.GROOMING);
                if (!checkContributes(horse, player, needs, state, slot)) {
                    return InteractionResult.SUCCESS_SERVER;
                }

                stack.hurtAndBreak(1, player, hand);
                spawnParticles(horse, ParticleTypes.HAPPY_VILLAGER, 4);
                if (horse.getRandom().nextInt(3) == 0) {
                    playAnimation(horse, HorseAnimation.NUZZLE);
                }
                addPoints(horse, needs, state, slot, 1.0F, now);
            }
        }

        finishContribution(horse, player, needs, state);
        return InteractionResult.SUCCESS_SERVER;
    }

    public static boolean onRideAttempt(AbstractHorse horse, ServerPlayer player) {
        if (!isBeingTamed(horse)) {
            return false;
        }

        AdvancedHorseAccess access = access(horse);
        TamingState state = access.advancedhorses$getTamingState();
        int needs = access.advancedhorses$getSyncedNeeds();
        int slot = slotOf(needs, TamingNeed.TRUST);
        if (slot >= 0 && !isFulfilled(horse, needs, state, slot)) {
            addPoints(horse, needs, state, slot, 1.0F, horse.level().getGameTime());
            finishContribution(horse, player, needs, state);
        }
        return horse.isTamed();
    }

    public static void onHurt(AbstractHorse horse, DamageSource source) {
        if (!isBeingTamed(horse) || !(source.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        AdvancedHorseAccess access = access(horse);
        TamingState state = access.advancedhorses$getTamingState();
        int needs = access.advancedhorses$getSyncedNeeds();
        for (int slot = 0; slot < TamingData.SLOTS; slot++) {
            state.points[slot] = Math.max(0.0F, state.points[slot] - required(horse, needs, slot) * HURT_PENALTY);
        }
        state.lastProgressTime = horse.level().getGameTime();
        sync(horse, needs, state);
        player.sendOverlayMessage(Component.translatable("advancedhorses.hurt").withStyle(ChatFormatting.RED));
    }

    private static void tickCompany(AbstractHorse horse, ServerLevel level, int needs, TamingState state) {
        int slot = slotOf(needs, TamingNeed.COMPANY);
        if (slot < 0 || isFulfilled(horse, needs, state, slot)) {
            return;
        }

        Player player = level.getNearestPlayer(horse.getX(), horse.getY(), horse.getZ(), COMPANY_RANGE,
            entity -> entity instanceof Player p && p.isCrouching() && !p.isSpectator() && !p.isPassenger());
        if (player instanceof ServerPlayer serverPlayer) {
            addPoints(horse, needs, state, slot, 1.0F, level.getGameTime());
            if (isFulfilled(horse, needs, state, slot) || horse.tickCount % 200 == 0) {
                finishContribution(horse, serverPlayer, needs, state);
            } else {
                completeIfDone(horse, serverPlayer, needs, state);
            }
        }
    }

    private static void tickIdleAnimations(AbstractHorse horse, ServerLevel level, TamingState state) {
        if (horse.isInWater()) {
            state.wasInWater = true;
        } else if (state.wasInWater && horse.onGround()) {
            state.wasInWater = false;
            playAnimation(horse, HorseAnimation.BODY_SHAKE);
            return;
        }

        if (horse.isVehicle() || horse.isEating() || horse.isStanding() || !horse.onGround()
            || access(horse).advancedhorses$getRest() != HorseRest.NONE) {
            return;
        }

        RandomSource random = horse.getRandom();
        if (random.nextInt(900) == 0) {
            playAnimation(horse, HorseAnimation.HEAD_SHAKE);
        } else if (!horse.isTamed() && random.nextInt(500) == 0 && level.getNearestPlayer(horse, NERVOUS_RANGE) != null) {
            playAnimation(horse, HorseAnimation.PAW);
        } else if (random.nextInt(2400) == 0) {
            playAnimation(horse, HorseAnimation.PAW);
        } else if (horse.isTamed() && horse.isSaddled() && random.nextInt(1500) == 0) {
            playAnimation(horse, random.nextBoolean() ? HorseAnimation.HOOF_STAMP : HorseAnimation.HEAD_TOSS);
        } else if (random.nextInt(3000) == 0) {
            playAnimation(horse, HorseAnimation.HOOF_STAMP);
        }
    }

    public static void playAnimation(AbstractHorse horse, HorseAnimation animation) {
        horse.level().broadcastEntityEvent(horse, animation.eventId());
    }

    private static boolean checkContributes(AbstractHorse horse, ServerPlayer player, int needs, TamingState state, int slot) {
        if (slot < 0) {
            player.sendOverlayMessage(Component.translatable("advancedhorses.not_needed").withStyle(ChatFormatting.GRAY));
            return false;
        }
        if (isFulfilled(horse, needs, state, slot)) {
            player.sendOverlayMessage(Component.translatable("advancedhorses.fulfilled").withStyle(ChatFormatting.GRAY));
            return false;
        }
        return true;
    }

    private static void finishContribution(AbstractHorse horse, ServerPlayer player, int needs, TamingState state) {
        if (completeIfDone(horse, player, needs, state)) {
            return;
        }
        int percent = Math.round(TamingData.totalProgress(access(horse).advancedhorses$getSyncedProgress()) * 100.0F);
        player.sendOverlayMessage(Component.translatable("advancedhorses.progress", percent).withStyle(ChatFormatting.GOLD));
    }

    private static boolean completeIfDone(AbstractHorse horse, ServerPlayer player, int needs, TamingState state) {
        for (int slot = 0; slot < TamingData.SLOTS; slot++) {
            if (!isFulfilled(horse, needs, state, slot)) {
                return false;
            }
        }

        horse.tameWithName(player);
        horse.level().playSound(null, horse.getX(), horse.getY(), horse.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.8F, 1.2F);
        playAnimation(horse, HorseAnimation.NUZZLE);
        player.sendOverlayMessage(Component.translatable("advancedhorses.tamed", horse.getName()).withStyle(ChatFormatting.GREEN));
        access(horse).advancedhorses$setSyncedNeeds(0);
        access(horse).advancedhorses$setSyncedProgress(0);
        state.reset();
        ModNetworking.sendNamePrompt(player, horse);
        return true;
    }

    private static void addPoints(AbstractHorse horse, int needs, TamingState state, int slot, float amount, long now) {
        state.points[slot] = Math.min(required(horse, needs, slot), state.points[slot] + amount);
        state.lastProgressTime = now;
        sync(horse, needs, state);
    }

    private static void sync(AbstractHorse horse, int needs, TamingState state) {
        float[] fractions = new float[TamingData.SLOTS];
        for (int slot = 0; slot < TamingData.SLOTS; slot++) {
            fractions[slot] = state.points[slot] / required(horse, needs, slot);
        }
        access(horse).advancedhorses$setSyncedProgress(TamingData.packProgress(fractions));
    }

    private static float required(AbstractHorse horse, int needs, int slot) {
        TamingNeed need = TamingData.need(needs, slot);
        return need == null ? 1.0F : required(horse, need);
    }

    public static float required(AbstractHorse horse, TamingNeed need) {
        float effort = ModRules.factor(horse.level(), ModRules.TAMING_EFFORT)
            * HorseStats.breed(horse).taming() * HorseStats.trait(horse).taming();
        return Math.max(1.0F, (float) Math.ceil(need.required(horse.isBred()) * effort));
    }

    private static boolean isFulfilled(AbstractHorse horse, int needs, TamingState state, int slot) {
        return state.points[slot] >= required(horse, needs, slot);
    }

    private static int slotOf(int needs, TamingNeed need) {
        for (int slot = 0; slot < TamingData.SLOTS; slot++) {
            if (TamingData.need(needs, slot) == need) {
                return slot;
            }
        }
        return -1;
    }

    private static float foodValue(ItemStack stack) {
        if (stack.is(Items.SUGAR)) {
            return 1.0F;
        } else if (stack.is(Items.WHEAT)) {
            return 2.0F;
        } else if (stack.is(Items.APPLE) || stack.is(Items.CARROT)) {
            return 3.0F;
        } else if (stack.is(Items.GOLDEN_CARROT)) {
            return 5.0F;
        } else if (stack.is(Items.GOLDEN_APPLE)) {
            return 8.0F;
        } else if (stack.is(Items.HAY_BLOCK)) {
            return 9.0F;
        } else if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            return 12.0F;
        }
        return 1.0F;
    }

    private static void spawnParticles(AbstractHorse horse, ParticleOptions particle, int count) {
        if (horse.level() instanceof ServerLevel level) {
            level.sendParticles(particle, horse.getX(), horse.getY() + horse.getBbHeight() * 0.9, horse.getZ(), count, 0.4, 0.3, 0.4, 0.02);
        }
    }
}
