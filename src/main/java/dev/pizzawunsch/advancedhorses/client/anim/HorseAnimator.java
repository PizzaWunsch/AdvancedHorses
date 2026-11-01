package dev.pizzawunsch.advancedhorses.client.anim;

import dev.pizzawunsch.advancedhorses.anim.GaitPattern;
import dev.pizzawunsch.advancedhorses.anim.HorseAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class HorseAnimator {
    private static final float TWO_PI = Mth.TWO_PI;

    private static final float[] AMP_FRONT = {0.45F, 0.45F, 0.65F, 0.85F, 1.0F, 1.15F};
    private static final float[] AMP_HIND = {0.4F, 0.4F, 0.55F, 0.7F, 0.85F, 1.0F};
    private static final float[] FLEX = {0.1F, 0.1F, 0.16F, 0.16F, 0.18F, 0.22F};
    private static final float[] BOUNCE = {0.25F, 0.25F, 0.75F, 0.9F, 1.1F, 1.0F};
    private static final float[] ROCK = {0.0F, 0.0F, 0.0F, 0.06F, 0.08F, 0.05F};
    private static final float[] NOD = {0.09F, 0.09F, 0.02F, 0.1F, 0.12F, 0.08F};
    private static final float[] NOD_FREQUENCY = {2.0F, 2.0F, 2.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] SWAY = {0.025F, 0.025F, 0.01F, 0.0F, 0.0F, 0.0F};
    private static final float[] TAIL_LIFT = {0.0F, 0.0F, 0.15F, 0.25F, 0.35F, 0.6F};
    private static final float[] HEAD_EXTEND = {0.0F, 0.0F, 0.05F, 0.1F, 0.15F, 0.35F};
    private static final float[] EARS_BACK = {0.0F, 0.0F, 0.0F, 0.1F, 0.2F, 0.6F};

    private static final Vector3f PIVOT = new Vector3f();
    private static final Vector3f ROTATED = new Vector3f();
    private static final Quaternionf ROTATION = new Quaternionf();

    private HorseAnimator() {
    }

    public static void animate(EquineRenderState state, HorseAnimState data, ModelPart root, ModelPart body, ModelPart headParts,
                               ModelPart leftFront, ModelPart rightFront, ModelPart leftHind, ModelPart rightHind, ModelPart tail) {
        float age = state.ageInTicks;
        float seed = (data.seed % 1000) * 0.37F;
        float standing = state.standAnimation;
        float free = 1.0F - standing;
        float stride = Math.min(1.0F, state.walkAnimationSpeed * 1.6F) * (1.0F - data.rest);
        float phase = data.phase;
        ModelPart[] legs = {leftFront, rightFront, leftHind, rightHind};

        boolean legsAtRoot = leftFront.getInitialPose().y() > body.getInitialPose().y();

        if (state.walkAnimationSpeed > 0.2F) {
            float headWeight = 1.0F - Math.max(standing, state.eatAnimation) + standing;
            headParts.xRot -= Mth.cos(state.walkAnimationPos * 0.8F) * 0.15F * state.walkAnimationSpeed * headWeight;
        }

        float[] angles = new float[4];
        float[] swings = new float[4];
        float bounce = 0.0F;
        float rock = 0.0F;
        float nod = 0.0F;
        float sway = 0.0F;
        float tailLift = 0.0F;
        float headExtend = 0.0F;
        float earsBack = 0.0F;
        float flex = 0.0F;
        float[] weights = data.gaitWeights;
        for (int gait = 0; gait < weights.length; gait++) {
            float w = weights[gait];
            if (w < 0.001F) {
                continue;
            }
            for (int leg = 0; leg < 4; leg++) {
                float amp = leg < 2 ? AMP_FRONT[gait] : AMP_HIND[gait];
                angles[leg] += w * amp * legWave(phase, GaitPattern.PHASE[gait][leg]);
                swings[leg] += w * swingPhase(phase, GaitPattern.PHASE[gait][leg]);
            }
            bounce += w * BOUNCE[gait] * bounceShape(gait, phase);
            rock += w * ROCK[gait] * Mth.sin(phase + 0.6F);
            nod += w * NOD[gait] * Mth.cos(phase * NOD_FREQUENCY[gait] + 0.3F);
            sway += w * SWAY[gait] * Mth.sin(phase);
            tailLift += w * TAIL_LIFT[gait];
            headExtend += w * HEAD_EXTEND[gait];
            earsBack += w * EARS_BACK[gait];
            flex += w * FLEX[gait];
        }
        float moving = stride * free;
        earsBack *= moving;

        for (int i = 0; i < 4; i++) {
            legs[i].xRot = Mth.lerp(free, legs[i].xRot, angles[i] * stride);

            legs[i].yScale = 1.0F - flex * swings[i] * moving;
        }

        if (data.lame) {
            float load = Math.max(0.0F, -Mth.sin(GaitPattern.legPhase(phase, 2, 1)));
            rightFront.xRot *= 0.55F;
            rightFront.yScale = Math.min(rightFront.yScale, 1.0F - 0.12F * moving);
            headParts.xRot -= 0.2F * load * moving;
            body.zRot += 0.04F * load * moving;
        }

        bounce *= moving * (1.0F - data.swim);
        body.y -= bounce;
        headParts.y -= bounce;
        if (legsAtRoot) {
            for (ModelPart leg : legs) {
                leg.y -= bounce * 0.5F;
            }
        }
        body.xRot += rock * moving;
        body.zRot += sway * moving;
        headParts.xRot += (nod + headExtend) * moving;
        tail.xRot += tailLift * moving + Mth.sin(phase * 2.0F) * 0.08F * tailLift * moving;

        float swim = data.swim * free;
        if (swim > 0.001F) {
            for (int i = 0; i < 4; i++) {
                float paddle = Mth.sin(age * 0.35F + (i % 2 == 0 ? 0.0F : Mth.PI) + (i >= 2 ? Mth.HALF_PI : 0.0F));
                float target = i < 2 ? -0.35F + paddle * 0.8F : 0.25F + paddle * 0.6F;
                legs[i].xRot = Mth.lerp(swim, legs[i].xRot, target);
                legs[i].yScale = Mth.lerp(swim, legs[i].yScale, 0.85F + 0.1F * paddle);
            }
            headParts.xRot -= 0.35F * swim;
            body.xRot -= 0.08F * swim;
            tail.xRot += 0.5F * swim;
        }

        float air = data.air * free * (1.0F - data.swim);
        if (air > 0.001F) {
            float rising = Mth.clamp(data.verticalSpeed * 4.0F, -1.0F, 1.0F);
            float frontTarget = rising > 0.0F ? -1.1F : Mth.lerp(-rising, -1.1F, -0.55F);
            float hindTarget = rising > 0.0F ? 0.9F : Mth.lerp(-rising, 0.9F, -0.3F);
            for (int i = 0; i < 4; i++) {
                boolean front = i < 2;
                legs[i].xRot = Mth.lerp(air, legs[i].xRot, front ? frontTarget : hindTarget);
                legs[i].yScale = Mth.lerp(air, legs[i].yScale, front ? (rising > 0.0F ? 0.7F : 0.9F) : 0.95F);
            }
            body.xRot -= 0.22F * rising * air;
            headParts.xRot += 0.2F * air;
            tail.xRot += 0.5F * air;
            earsBack = Math.max(earsBack, 0.3F * air);
        }

        float lean = -data.turn * moving * (1.0F - air);
        body.zRot += 0.12F * lean;
        headParts.zRot += 0.06F * lean;

        float exhausted = data.side;
        float breathRate = 0.11F + 0.14F * exhausted + 0.06F * data.droop;
        float breath = Mth.sin(age * breathRate + seed);
        float breathScale = 1.0F + (0.008F + 0.022F * exhausted) * breath * (1.0F - stride * 0.5F);
        body.xScale *= breathScale;
        body.yScale *= breathScale;

        float idle = (1.0F - stride) * free * (1.0F - state.eatAnimation) * (1.0F - data.rest);
        float idleSpeed = 1.0F - 0.5F * data.droop;
        headParts.yRot += Mth.sin(age * 0.021F * idleSpeed + seed) * 0.12F * idle;
        headParts.xRot += Mth.sin(age * 0.033F * idleSpeed + seed * 2.0F) * 0.05F * idle;

        float droop = data.droop * free * (1.0F - data.rest);
        headParts.xRot += 0.3F * droop * (1.0F - stride * 0.5F);
        tail.xRot -= 0.2F * droop;

        float eat = state.eatAnimation;
        if (eat > 0.0F) {
            headParts.xRot += Mth.sin(age * 0.9F) * 0.04F * eat;
            headParts.yRot += Mth.sin(age * 0.12F + seed) * 0.1F * eat;
        }

        tail.yRot *= 0.65F;
        tail.yRot += Mth.sin(age * 0.05F + seed) * 0.07F * (1.0F - exhausted) + Mth.sin(phase) * 0.1F * moving;
        tail.zRot += Mth.sin(age * 0.07F + seed * 1.3F) * 0.05F * (1.0F - exhausted);
        tail.yRot += data.tailSwing * (1.0F - data.rest);
        tail.xRot += data.tailLift * (1.0F - data.rest);
        animateMane(headParts, age, seed, data.speed, data.rest);

        animateRest(data, age, seed, root, body, headParts, legs, tail, legsAtRoot);

        float earsForward = 0.0F;
        earsBack = Math.max(earsBack, standing);
        float upright = free * (1.0F - data.rest);

        float t = data.progress(HorseAnimation.HEAD_SHAKE);
        if (t >= 0.0F) {
            float env = envelope(t, 0.15F) * free;
            headParts.zRot += Mth.sin(t * TWO_PI * 3.0F) * 0.28F * env;
            headParts.yRot += Mth.sin(t * TWO_PI * 3.0F + 0.5F) * 0.18F * env;
            earsBack = Math.max(earsBack, 0.4F * env);
        }

        t = data.progress(HorseAnimation.PAW);
        if (t >= 0.0F) {
            float env = envelope(t, 0.2F) * upright;
            float scrape = Mth.sin(t * TWO_PI * 2.0F);
            rightFront.xRot = Mth.lerp(env, rightFront.xRot, -0.55F + 0.45F * scrape);
            rightFront.yScale = Mth.lerp(env, rightFront.yScale, 0.85F + 0.07F * scrape);
            headParts.xRot += 0.25F * env;
            earsBack = Math.max(earsBack, 0.25F * env);
        }

        t = data.progress(HorseAnimation.BUCK);
        if (t >= 0.0F) {
            float kick = Mth.sin(t * Mth.PI) * upright;
            body.xRot += 0.3F * kick;
            leftHind.xRot += 0.9F * kick;
            rightHind.xRot += 0.95F * kick;
            leftHind.y -= 2.5F * kick;
            rightHind.y -= 2.5F * kick;
            leftFront.xRot -= 0.15F * kick;
            rightFront.xRot -= 0.15F * kick;
            headParts.xRot += 0.35F * kick;
            tail.xRot += 0.8F * kick;
            earsBack = Math.max(earsBack, kick);
        }

        t = data.progress(HorseAnimation.NUZZLE);
        if (t >= 0.0F) {
            float env = envelope(t, 0.25F) * free;
            headParts.xRot += 0.35F * env * (1.0F - data.rest);
            headParts.yRot += Mth.sin(t * TWO_PI) * 0.2F * env;
            headParts.zRot += Mth.sin(t * TWO_PI * 1.5F) * 0.12F * env;
            tail.yRot += Mth.sin(t * TWO_PI * 2.0F) * 0.25F * env;
            earsForward = Math.max(earsForward, env);
        }

        t = data.progress(HorseAnimation.BODY_SHAKE);
        if (t >= 0.0F) {
            float env = envelope(t, 0.15F) * upright;
            float wave = Mth.sin(t * TWO_PI * 4.0F) * env;
            body.zRot += 0.14F * wave;
            headParts.zRot += 0.2F * Mth.sin(t * TWO_PI * 4.0F + 0.8F) * env;
            tail.yRot += 0.5F * wave;
            earsBack = Math.max(earsBack, 0.3F * env);
        }

        t = data.progress(HorseAnimation.STRETCH);
        if (t >= 0.0F) {
            float env = envelope(t, 0.3F) * upright;
            leftFront.xRot -= 0.5F * env;
            rightFront.xRot -= 0.5F * env;
            leftHind.xRot += 0.35F * env;
            rightHind.xRot += 0.35F * env;
            body.y += 1.5F * env;
            headParts.y += 1.5F * env;
            headParts.xRot += 0.5F * env;
            tail.xRot += 0.3F * env;
            earsBack = Math.max(earsBack, 0.3F * env);
        }

        t = data.progress(HorseAnimation.HOOF_STAMP);
        if (t >= 0.0F) {
            float env = envelope(t, 0.1F) * upright;
            float lift = Math.max(0.0F, Mth.sin(t * TWO_PI * 2.0F)) * env;
            rightHind.xRot -= 0.4F * lift;
            rightHind.yScale -= 0.15F * lift;
            rightHind.y -= 0.8F * lift;
        }

        t = data.progress(HorseAnimation.HEAD_TOSS);
        if (t >= 0.0F) {
            float toss = Mth.sin(t * Mth.PI) * free;
            headParts.xRot -= 0.5F * toss;
            headParts.zRot += Mth.sin(t * TWO_PI) * 0.1F * toss;
            earsBack = Math.max(earsBack, 0.5F * toss);
        }

        if (data.wild) {
            earsForward = Math.max(earsForward, 0.2F * (1.0F - earsBack));
        }

        animateEars(headParts, age, data.seed, data.wild, earsBack, earsForward, data.droop, data.rest);
    }

    private static void animateRest(HorseAnimState data, float age, float seed, ModelPart root, ModelPart body, ModelPart headParts,
                                    ModelPart[] legs, ModelPart tail, boolean legsAtRoot) {
        float rest = smoothstep(0.0F, 1.0F, data.rest);
        if (rest <= 0.001F) {
            return;
        }

        float side = smoothstep(0.0F, 1.0F, data.side);
        float belly = rest * (1.0F - side);
        float drop = (legsAtRoot ? 8.0F : 4.0F) * belly;

        body.y += drop;
        headParts.y += drop;
        if (legsAtRoot) {
            for (ModelPart leg : legs) {
                leg.y += drop;
            }
        }

        for (int i = 0; i < 4; i++) {
            boolean front = i < 2;
            float folded = front ? 1.45F : -1.45F;
            float stretched = front ? -0.3F : 0.3F;
            legs[i].xRot = Mth.lerp(rest, legs[i].xRot, Mth.lerp(side, folded, stretched + 0.08F * Mth.sin(age * 0.05F + i)));
            legs[i].yScale = Mth.lerp(belly, legs[i].yScale, 0.75F);
        }

        float sleep = smoothstep(0.2F, 0.7F, Mth.sin(age * 0.008F + seed)) * belly;
        headParts.xRot += Mth.lerp(sleep, -0.15F, 1.0F) * belly;
        headParts.y += 4.0F * sleep;
        tail.xRot -= 0.3F * rest;

        if (side > 0.001F) {
            rotateAround(root, -5.0F, 13.0F, 0.0F, 0.0F, 0.0F, -Mth.HALF_PI * side);
            root.y += 11.0F * side;
            headParts.xRot -= 0.25F * side;
        }
    }

    private static void animateMane(ModelPart headParts, float age, float seed, float speed, float rest) {
        if (!headParts.hasChild("mane")) {
            return;
        }
        ModelPart mane = headParts.getChild("mane");
        float wind = Math.min(1.0F, speed * 3.0F) * (1.0F - rest);
        float flutter = Mth.sin(age * 1.1F + seed) * 0.03F + Mth.sin(age * 2.3F + seed * 0.5F) * 0.015F;
        float xRot = -(0.12F + flutter) * wind;
        if (mane.getInitialPose().equals(PartPose.ZERO)) {
            rotateAround(mane, 0.0F, 5.0F, 6.0F, xRot, 0.0F, 0.0F);
        } else {
            mane.xRot += xRot;
        }
    }

    private static void animateEars(ModelPart headParts, float age, int seed, boolean wild, float back, float forward, float droop, float rest) {
        if (!headParts.hasChild("head")) {
            return;
        }
        ModelPart head = headParts.getChild("head");
        if (!head.hasChild("left_ear") || !head.hasChild("right_ear")) {
            return;
        }

        int interval = wild ? 24 : rest > 0.5F ? 70 : 40;
        int slot = Mth.floor(age / interval);
        float local = (age - slot * interval) / 8.0F;
        float relaxed = Math.max(droop, rest * 0.6F);
        for (int side = 0; side < 2; side++) {
            ModelPart ear = head.getChild(side == 0 ? "left_ear" : "right_ear");
            float sign = side == 0 ? 1.0F : -1.0F;

            float twitch = 0.0F;
            if (local < 1.0F && hash(seed, slot, side) < 0.3F) {
                twitch = Mth.sin(local * Mth.PI);
            }

            float xRot = -0.7F * back + 0.25F * forward - 0.35F * twitch - 0.2F * relaxed;

            float zRot = sign * (0.15F * back + 0.2F * twitch + 0.35F * relaxed);
            float yRot = sign * 0.25F * twitch;

            if (ear.getInitialPose().equals(PartPose.ZERO)) {
                rotateAround(ear, sign * 1.55F, -10.0F, 4.5F, xRot, yRot, zRot);
            } else {
                ear.xRot += xRot;
                ear.yRot += yRot;
                ear.zRot += zRot;
            }
        }
    }

    private static void rotateAround(ModelPart part, float px, float py, float pz, float xRot, float yRot, float zRot) {
        part.xRot += xRot;
        part.yRot += yRot;
        part.zRot += zRot;
        ROTATION.rotationZYX(part.zRot, part.yRot, part.xRot);
        PIVOT.set(px, py, pz);
        ROTATION.transform(PIVOT, ROTATED);
        part.x += PIVOT.x - ROTATED.x;
        part.y += PIVOT.y - ROTATED.y;
        part.z += PIVOT.z - ROTATED.z;
    }

    private static float legWave(float cycle, float phase) {
        float p = cycle + Mth.PI + phase * TWO_PI;
        return Mth.cos(p - 0.3F * Mth.sin(p));
    }

    private static float swingPhase(float cycle, float phase) {
        float p = cycle + Mth.PI + phase * TWO_PI;
        return Math.max(0.0F, Mth.sin(p));
    }

    private static float bounceShape(int gait, float phase) {
        if (gait <= 1) {
            return Math.abs(Mth.sin(phase * 2.0F));
        } else if (gait == 2) {
            return Math.abs(Mth.sin(phase));
        }
        return Math.max(0.0F, Mth.sin(phase + 0.4F));
    }

    private static float envelope(float t, float fade) {
        return smoothstep(0.0F, fade, t) * smoothstep(0.0F, fade, 1.0F - t);
    }

    private static float smoothstep(float edge0, float edge1, float x) {
        float t = Mth.clamp((x - edge0) / (edge1 - edge0), 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }

    private static float hash(int seed, int a, int b) {
        int h = seed * 73856093 ^ a * 19349663 ^ b * 83492791;
        h ^= h >>> 13;
        h *= 0x5bd1e995;
        h ^= h >>> 15;
        return (h & 0xFFFF) / 65535.0F;
    }
}
