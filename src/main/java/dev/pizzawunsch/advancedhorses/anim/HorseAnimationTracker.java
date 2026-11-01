package dev.pizzawunsch.advancedhorses.anim;

import dev.pizzawunsch.advancedhorses.horse.HorseGait;
import dev.pizzawunsch.advancedhorses.horse.HorseRest;
import dev.pizzawunsch.advancedhorses.horse.HorseStat;
import dev.pizzawunsch.advancedhorses.horse.HorseStats;
import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import java.util.Arrays;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public final class HorseAnimationTracker {
    private static final int NOT_PLAYING = Integer.MIN_VALUE / 2;
    private static final int GAITS = HorseGait.values().length;

    private static final float[] STRIDE_LENGTH = {2.2F, 2.2F, 3.0F, 4.0F, 5.0F, 5.8F};
    private static final float[][] HOOF_OFFSET = {{0.25F, 0.6F}, {-0.25F, 0.6F}, {0.25F, -0.45F}, {-0.25F, -0.45F}};

    private final int[] startTicks = new int[HorseAnimation.values().length];
    private final boolean[] swinging = new boolean[4];

    public final float[] gaitWeights = new float[GAITS];
    public float phase;
    public float phaseO;
    public float rest;
    public float restO;
    public float side;
    public float sideO;
    public float air;
    public float airO;
    public float swim;
    public float swimO;
    public float turn;
    public float verticalSpeed;
    public float droop;
    public float speed;
    public float tailSwing;
    public float tailSwingO;
    public float tailLift;
    public float tailLiftO;

    private float tailSwingVelocity;
    private float tailLiftVelocity;
    private float lastBodyRot;
    private boolean initialized;

    public HorseAnimationTracker() {
        Arrays.fill(this.startTicks, NOT_PLAYING);
        this.gaitWeights[HorseGait.WALK.ordinal()] = 1.0F;
    }

    public void start(HorseAnimation animation, AbstractHorse horse) {
        this.startTicks[animation.ordinal()] = horse.tickCount;
        float pitch = horse.isBaby() ? 1.4F : 1.0F;
        switch (animation) {
            case HEAD_SHAKE -> playLocal(horse, SoundEvents.HORSE_BREATHE, 0.9F, 1.25F * pitch);
            case BODY_SHAKE -> playLocal(horse, SoundEvents.HORSE_BREATHE, 0.8F, 0.9F * pitch);
            case STRETCH -> playLocal(horse, SoundEvents.HORSE_BREATHE, 0.7F, 0.75F * pitch);
            case HOOF_STAMP, PAW -> playLocal(horse, SoundEvents.HORSE_STEP, 0.35F, 0.9F);
            default -> {
            }
        }
    }

    public float progress(HorseAnimation animation, int tickCount, float partialTick) {
        float elapsed = tickCount - this.startTicks[animation.ordinal()] + partialTick;
        if (elapsed < 0.0F || elapsed >= animation.duration()) {
            return -1.0F;
        }
        return elapsed / animation.duration();
    }

    public void clientTick(AbstractHorse horse) {
        this.phaseO = this.phase;
        this.restO = this.rest;
        this.sideO = this.side;
        this.airO = this.air;
        this.swimO = this.swim;
        this.tailSwingO = this.tailSwing;
        this.tailLiftO = this.tailLift;

        double dx = horse.getX() - horse.xo;
        double dz = horse.getZ() - horse.zo;
        float distance = (float) Math.sqrt(dx * dx + dz * dz);
        if (distance > 4.0F) {
            distance = 0.0F;
        }
        float acceleration = distance - this.speed;
        this.speed = distance;

        int target = this.targetGait(horse, distance);
        float strideLength = 0.0F;
        int dominant = 0;
        for (int i = 0; i < GAITS; i++) {
            this.gaitWeights[i] += ((i == target ? 1.0F : 0.0F) - this.gaitWeights[i]) * 0.15F;
            strideLength += this.gaitWeights[i] * STRIDE_LENGTH[i];
            if (this.gaitWeights[i] > this.gaitWeights[dominant]) {
                dominant = i;
            }
        }

        float water = horse.isInWater() ? 0.6F : 1.0F;
        this.phase += distance / Math.max(0.5F, strideLength) * Mth.TWO_PI * water;
        if (this.phase > Mth.TWO_PI * 1000.0F) {
            this.phase -= Mth.TWO_PI * 1000.0F;
            this.phaseO -= Mth.TWO_PI * 1000.0F;
        }

        int rest = TamingManager.access(horse).advancedhorses$getRest();
        this.rest = approach(this.rest, rest != HorseRest.NONE ? 1.0F : 0.0F, 0.035F);
        this.side = approach(this.side, rest == HorseRest.EXHAUSTED ? 1.0F : 0.0F, 0.025F);
        if (!this.initialized) {
            this.rest = this.restO = rest != HorseRest.NONE ? 1.0F : 0.0F;
            this.side = this.sideO = rest == HorseRest.EXHAUSTED ? 1.0F : 0.0F;
            this.lastBodyRot = horse.yBodyRot;
            this.initialized = true;
        }

        boolean airborne = !horse.onGround() && !horse.isInWater() && (horse.isVehicle() || horse.getDeltaMovement().y > 0.05);
        this.air += ((airborne ? 1.0F : 0.0F) - this.air) * 0.3F;
        this.verticalSpeed += ((float) horse.getDeltaMovement().y - this.verticalSpeed) * 0.4F;
        this.swim += ((horse.isInWater() && !horse.onGround() ? 1.0F : 0.0F) - this.swim) * 0.15F;

        float turnDelta = Mth.wrapDegrees(horse.yBodyRot - this.lastBodyRot);
        this.lastBodyRot = horse.yBodyRot;
        float speedFactor = Math.min(1.0F, distance * 4.0F);
        this.turn += (Mth.clamp(turnDelta / 12.0F, -1.0F, 1.0F) * speedFactor - this.turn) * 0.2F;

        this.tailSwingVelocity += -0.2F * this.tailSwing - 0.35F * this.tailSwingVelocity - Mth.clamp(turnDelta, -30.0F, 30.0F) * 0.006F;
        this.tailSwing = Mth.clamp(this.tailSwing + this.tailSwingVelocity, -0.8F, 0.8F);
        this.tailLiftVelocity += -0.2F * this.tailLift - 0.35F * this.tailLiftVelocity + Mth.clamp(acceleration, -0.3F, 0.3F) * 1.2F;
        this.tailLift = Mth.clamp(this.tailLift + this.tailLiftVelocity, -0.5F, 0.6F);

        float hunger = HorseStats.get(horse, HorseStat.HUNGER);
        float droopTarget = horse.isTamed() && hunger < 30.0F ? (30.0F - hunger) / 30.0F : 0.0F;
        this.droop += (droopTarget - this.droop) * 0.05F;

        this.footfalls(horse, dominant, distance);
    }

    private void footfalls(AbstractHorse horse, int gait, float distance) {
        boolean active = distance > 0.02F && horse.onGround() && this.rest < 0.1F && TamingManager.isTameableType(horse);
        for (int leg = 0; leg < 4; leg++) {
            boolean swing = GaitPattern.isSwinging(this.phase, gait, leg);
            if (active && this.swinging[leg] && !swing) {
                this.strike(horse, gait, leg);
            }
            this.swinging[leg] = swing;
        }
    }

    private void strike(AbstractHorse horse, int gait, int leg) {
        Level level = horse.level();
        RandomSource random = horse.getRandom();
        float scale = horse.isBaby() ? 0.6F : 1.0F;
        float yaw = horse.yBodyRot * Mth.DEG_TO_RAD;
        float side = HOOF_OFFSET[leg][0] * scale;
        float forward = HOOF_OFFSET[leg][1] * scale;
        double x = horse.getX() - Mth.sin(yaw) * forward + Mth.cos(yaw) * side;
        double z = horse.getZ() + Mth.cos(yaw) * forward + Mth.sin(yaw) * side;
        double y = horse.getY();

        if (horse.isInWater()) {
            for (int i = 0; i < 3; i++) {
                level.addParticle(ParticleTypes.SPLASH, x, y + 0.2, z, random.nextGaussian() * 0.1, 0.2, random.nextGaussian() * 0.1);
            }
            if (leg == 0) {
                level.playLocalSound(x, y, z, SoundEvents.GENERIC_SPLASH, SoundSource.NEUTRAL, 0.15F, 1.2F + random.nextFloat() * 0.3F, false);
            }
            return;
        }

        BlockState ground = horse.getBlockStateOn();
        if (ground.isAir()) {
            return;
        }
        SoundType soundType = ground.getSoundType();
        boolean fast = gait >= HorseGait.CANTER.ordinal();
        SoundEvent sound;
        float volume = soundType.getVolume() * 0.15F;
        if (soundType == SoundType.WOOD || soundType == SoundType.NETHER_WOOD || soundType == SoundType.CHERRY_WOOD
            || soundType == SoundType.BAMBOO_WOOD || soundType == SoundType.STEM) {
            sound = SoundEvents.HORSE_STEP_WOOD;
        } else if (fast && leg == 0) {
            sound = SoundEvents.HORSE_GALLOP;
            volume = soundType.getVolume() * 0.2F;
        } else {
            sound = horse.isBaby() ? SoundEvents.HORSE_STEP_BABY : SoundEvents.HORSE_STEP;
            volume *= fast ? 0.7F : 1.0F;
        }
        level.playLocalSound(x, y, z, sound, SoundSource.NEUTRAL, volume, soundType.getPitch() * (0.95F + random.nextFloat() * 0.1F), false);

        boolean loose = ground.is(BlockTags.SAND) || ground.is(BlockTags.DIRT) || ground.getSoundType() == SoundType.GRAVEL
            || ground.getSoundType() == SoundType.SNOW;
        int count = fast ? gait - 1 : loose && gait == HorseGait.TROT.ordinal() ? 1 : 0;
        if (loose) {
            count *= 2;
        }
        BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, ground);
        for (int i = 0; i < count; i++) {
            level.addParticle(particle, x + random.nextGaussian() * 0.1, y + 0.05, z + random.nextGaussian() * 0.1,
                random.nextGaussian() * 0.05, 0.1 + random.nextFloat() * 0.12, random.nextGaussian() * 0.05);
        }
    }

    private static void playLocal(AbstractHorse horse, SoundEvent sound, float volume, float pitch) {
        horse.level().playLocalSound(horse.getX(), horse.getEyeY(), horse.getZ(), sound, horse.getSoundSource(), volume, pitch, false);
    }

    private int targetGait(AbstractHorse horse, float distance) {
        if (horse.getControllingPassenger() != null) {
            HorseGait gait = HorseGait.current(horse);
            return gait == HorseGait.STAND ? HorseGait.WALK.ordinal() : gait.ordinal();
        }

        double topSpeed = Math.max(0.05, horse.getAttributeValue(Attributes.MOVEMENT_SPEED) * 2.16);
        double ratio = distance / topSpeed;
        if (ratio < 0.75) {
            return HorseGait.WALK.ordinal();
        } else if (ratio < 0.95) {
            return HorseGait.TROT.ordinal();
        } else if (ratio < 1.1) {
            return HorseGait.CANTER.ordinal();
        }
        return HorseGait.GALLOP.ordinal();
    }

    private static float approach(float value, float target, float step) {
        return value < target ? Math.min(target, value + step) : Math.max(target, value - step);
    }
}
