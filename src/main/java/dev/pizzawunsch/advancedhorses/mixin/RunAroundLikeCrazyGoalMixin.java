package dev.pizzawunsch.advancedhorses.mixin;

import dev.pizzawunsch.advancedhorses.anim.HorseAnimation;
import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RunAroundLikeCrazyGoal.class)
public abstract class RunAroundLikeCrazyGoalMixin extends Goal {
    @Shadow
    @Final
    private AbstractHorse horse;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void advancedhorses$tick(CallbackInfo ci) {
        if (!TamingManager.isBeingTamed(this.horse)) {
            return;
        }

        ci.cancel();
        if (this.horse.getRandom().nextInt(this.adjustedTickDelay(50)) != 0) {
            return;
        }

        Entity passenger = this.horse.getFirstPassenger();
        if (passenger == null) {
            return;
        }
        if (passenger instanceof ServerPlayer player && TamingManager.onRideAttempt(this.horse, player)) {
            return;
        }

        this.horse.ejectPassengers();
        if (this.horse.getRandom().nextBoolean()) {
            this.horse.makeMad();
        } else {
            this.horse.playSound(SoundEvents.HORSE_ANGRY, 1.0F, 1.0F);
            TamingManager.playAnimation(this.horse, HorseAnimation.BUCK);
        }
        this.horse.level().broadcastEntityEvent(this.horse, (byte) 6);
    }
}
