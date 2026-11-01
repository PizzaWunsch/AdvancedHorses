package dev.pizzawunsch.advancedhorses.mixin;

import dev.pizzawunsch.advancedhorses.horse.HorseLife;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
    @Shadow
    private int repairPlayerItems(ServerPlayer player, int amount) {
        throw new AssertionError();
    }

    @Inject(method = "repairPlayerItems", at = @At("HEAD"), cancellable = true)
    private void advancedhorses$repairHorseshoes(ServerPlayer player, int amount, CallbackInfoReturnable<Integer> cir) {
        int remaining = HorseLife.repairShoesWithXp(player, amount);
        if (remaining != amount) {
            cir.setReturnValue(remaining > 0 ? this.repairPlayerItems(player, remaining) : 0);
        }
    }
}
