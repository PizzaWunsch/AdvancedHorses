package dev.pizzawunsch.advancedhorses.mixin.client;

import dev.pizzawunsch.advancedhorses.access.AdvancedHorseAccess;
import dev.pizzawunsch.advancedhorses.anim.HorseAnimation;
import dev.pizzawunsch.advancedhorses.anim.HorseAnimationTracker;
import dev.pizzawunsch.advancedhorses.client.anim.HorseAnimState;
import dev.pizzawunsch.advancedhorses.horse.HorseByte;
import dev.pizzawunsch.advancedhorses.horse.HorseStats;
import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorseRenderer.class)
public abstract class AbstractHorseRendererMixin {
    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/animal/equine/AbstractHorse;Lnet/minecraft/client/renderer/entity/state/EquineRenderState;F)V",
        at = @At("TAIL")
    )
    private void advancedhorses$extractAnimState(AbstractHorse entity, EquineRenderState state, float partialTicks, CallbackInfo ci) {
        HorseAnimState data = state.getData(HorseAnimState.KEY);
        if (data == null) {
            data = new HorseAnimState();
            state.setData(HorseAnimState.KEY, data);
        }

        HorseAnimationTracker tracker = ((AdvancedHorseAccess) entity).advancedhorses$getAnimations();
        for (HorseAnimation animation : HorseAnimation.values()) {
            data.progress[animation.ordinal()] = tracker.progress(animation, entity.tickCount, partialTicks);
        }
        System.arraycopy(tracker.gaitWeights, 0, data.gaitWeights, 0, data.gaitWeights.length);
        data.seed = entity.getId();
        data.wild = TamingManager.isBeingTamed(entity);
        data.phase = Mth.lerp(partialTicks, tracker.phaseO, tracker.phase);
        data.rest = Mth.lerp(partialTicks, tracker.restO, tracker.rest);
        data.side = Mth.lerp(partialTicks, tracker.sideO, tracker.side);
        data.air = Mth.lerp(partialTicks, tracker.airO, tracker.air);
        data.swim = Mth.lerp(partialTicks, tracker.swimO, tracker.swim);
        data.verticalSpeed = tracker.verticalSpeed;
        data.turn = tracker.turn;
        data.droop = tracker.droop;
        data.speed = tracker.speed;
        data.tailSwing = Mth.lerp(partialTicks, tracker.tailSwingO, tracker.tailSwing);
        data.tailLift = Mth.lerp(partialTicks, tracker.tailLiftO, tracker.tailLift);
        data.lame = HorseStats.hasFlag(entity, HorseByte.FLAG_LAME);
    }
}
