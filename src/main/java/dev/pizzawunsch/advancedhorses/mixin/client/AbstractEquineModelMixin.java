package dev.pizzawunsch.advancedhorses.mixin.client;

import dev.pizzawunsch.advancedhorses.client.anim.HorseAnimState;
import dev.pizzawunsch.advancedhorses.client.anim.HorseAnimator;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.equine.AbstractEquineModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractEquineModel.class)
public abstract class AbstractEquineModelMixin {
    @Shadow @Final protected ModelPart body;
    @Shadow @Final protected ModelPart headParts;
    @Shadow @Final protected ModelPart rightHindLeg;
    @Shadow @Final protected ModelPart leftHindLeg;
    @Shadow @Final protected ModelPart rightFrontLeg;
    @Shadow @Final protected ModelPart leftFrontLeg;
    @Shadow @Final private ModelPart tail;

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/EquineRenderState;)V", at = @At("TAIL"))
    private void advancedhorses$setupAnim(EquineRenderState state, CallbackInfo ci) {
        HorseAnimState data = state.getData(HorseAnimState.KEY);
        if (data != null) {
            ModelPart root = ((Model<?>) (Object) this).root();
            HorseAnimator.animate(state, data, root, this.body, this.headParts,
                this.leftFrontLeg, this.rightFrontLeg, this.leftHindLeg, this.rightHindLeg, this.tail);
        }
    }
}
