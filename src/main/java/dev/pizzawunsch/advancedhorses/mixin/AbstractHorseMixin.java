package dev.pizzawunsch.advancedhorses.mixin;

import dev.pizzawunsch.advancedhorses.access.AdvancedHorseAccess;
import dev.pizzawunsch.advancedhorses.anim.HorseAnimation;
import dev.pizzawunsch.advancedhorses.anim.HorseAnimationTracker;
import dev.pizzawunsch.advancedhorses.horse.HorseByte;
import dev.pizzawunsch.advancedhorses.horse.HorseGait;
import dev.pizzawunsch.advancedhorses.horse.HorseLife;
import dev.pizzawunsch.advancedhorses.horse.HorseRest;
import dev.pizzawunsch.advancedhorses.horse.HorseStat;
import dev.pizzawunsch.advancedhorses.horse.HorseStats;
import dev.pizzawunsch.advancedhorses.horse.Horseshoe;
import dev.pizzawunsch.advancedhorses.inventory.HorseshoeContainer;
import dev.pizzawunsch.advancedhorses.horse.LifeState;
import dev.pizzawunsch.advancedhorses.horse.ShoeEnchantments;
import dev.pizzawunsch.advancedhorses.taming.TamingData;
import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import dev.pizzawunsch.advancedhorses.taming.TamingState;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin implements AdvancedHorseAccess {
    @Unique
    private static final EntityDataAccessor<Integer> ADVANCEDHORSES_NEEDS = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Integer> ADVANCEDHORSES_PROGRESS = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Byte> ADVANCEDHORSES_GEAR = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.BYTE);
    @Unique
    private static final EntityDataAccessor<Byte> ADVANCEDHORSES_REST = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.BYTE);
    @Unique
    @SuppressWarnings("unchecked")
    private static final EntityDataAccessor<Float>[] ADVANCEDHORSES_STATS = new EntityDataAccessor[HorseStat.values().length];

    @Unique
    @SuppressWarnings("unchecked")
    private static final EntityDataAccessor<Byte>[] ADVANCEDHORSES_BYTES = new EntityDataAccessor[HorseByte.values().length];
    @Unique
    private static final int LEGACY_TRAINING_LEVEL = 30;
    @Unique
    private static final EntityDataAccessor<String> ADVANCEDHORSES_PEDIGREE = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.STRING);

    static {
        for (HorseStat stat : HorseStat.values()) {
            ADVANCEDHORSES_STATS[stat.ordinal()] = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.FLOAT);
        }
        for (HorseByte value : HorseByte.values()) {
            ADVANCEDHORSES_BYTES[value.ordinal()] = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.BYTE);
        }
    }

    @Shadow
    protected SimpleContainer inventory;

    @Unique
    private final TamingState advancedhorses$tamingState = new TamingState();
    @Unique
    private final LifeState advancedhorses$lifeState = new LifeState();
    @Unique
    private final HorseAnimationTracker advancedhorses$animations = new HorseAnimationTracker();
    @Unique
    private final HorseshoeContainer advancedhorses$shoes = new HorseshoeContainer(this::advancedhorses$onShoesChanged);

    @Unique
    private void advancedhorses$onShoesChanged() {
        AbstractHorse horse = this.advancedhorses$self();
        if (horse.level() != null && !horse.level().isClientSide()) {
            ItemStack stack = this.advancedhorses$shoes.getItem(0);
            ShoeEnchantments.update(horse, stack);
            int id = Horseshoe.of(stack).id();
            if (this.advancedhorses$getByte(HorseByte.SHOES) != id) {
                this.advancedhorses$setByte(HorseByte.SHOES, id);
                this.advancedhorses$lifeState.shoeWear = 0.0F;
                if (id != Horseshoe.NONE.id()) {
                    horse.playSound(SoundEvents.ANVIL_USE, 0.4F, 1.5F);
                }
            }
        }
    }

    @Unique
    private AbstractHorse advancedhorses$self() {
        return (AbstractHorse) (Object) this;
    }

    @Override
    public TamingState advancedhorses$getTamingState() {
        return this.advancedhorses$tamingState;
    }

    @Override
    public int advancedhorses$getSyncedNeeds() {
        return this.advancedhorses$self().getEntityData().get(ADVANCEDHORSES_NEEDS);
    }

    @Override
    public int advancedhorses$getSyncedProgress() {
        return this.advancedhorses$self().getEntityData().get(ADVANCEDHORSES_PROGRESS);
    }

    @Override
    public void advancedhorses$setSyncedNeeds(int needs) {
        this.advancedhorses$self().getEntityData().set(ADVANCEDHORSES_NEEDS, needs);
    }

    @Override
    public void advancedhorses$setSyncedProgress(int progress) {
        this.advancedhorses$self().getEntityData().set(ADVANCEDHORSES_PROGRESS, progress);
    }

    @Override
    public HorseAnimationTracker advancedhorses$getAnimations() {
        return this.advancedhorses$animations;
    }

    @Override
    public LifeState advancedhorses$getLifeState() {
        return this.advancedhorses$lifeState;
    }

    @Override
    public float advancedhorses$getStat(HorseStat stat) {
        return this.advancedhorses$self().getEntityData().get(ADVANCEDHORSES_STATS[stat.ordinal()]);
    }

    @Override
    public void advancedhorses$setStat(HorseStat stat, float value) {
        this.advancedhorses$self().getEntityData().set(ADVANCEDHORSES_STATS[stat.ordinal()], value);
    }

    @Override
    public int advancedhorses$getByte(HorseByte value) {
        return this.advancedhorses$self().getEntityData().get(ADVANCEDHORSES_BYTES[value.ordinal()]);
    }

    @Override
    public void advancedhorses$setByte(HorseByte value, int data) {
        this.advancedhorses$self().getEntityData().set(ADVANCEDHORSES_BYTES[value.ordinal()], (byte) data);
    }

    @Override
    public String advancedhorses$getPedigree() {
        return this.advancedhorses$self().getEntityData().get(ADVANCEDHORSES_PEDIGREE);
    }

    @Override
    public void advancedhorses$setPedigree(String pedigree) {
        this.advancedhorses$self().getEntityData().set(ADVANCEDHORSES_PEDIGREE, pedigree);
    }

    @Override
    public int advancedhorses$getGear() {
        return this.advancedhorses$self().getEntityData().get(ADVANCEDHORSES_GEAR);
    }

    @Override
    public void advancedhorses$setGear(int gear) {
        this.advancedhorses$self().getEntityData().set(ADVANCEDHORSES_GEAR, (byte) gear);
    }

    @Override
    public int advancedhorses$getRest() {
        return this.advancedhorses$self().getEntityData().get(ADVANCEDHORSES_REST);
    }

    @Override
    public void advancedhorses$setRest(int rest) {
        this.advancedhorses$self().getEntityData().set(ADVANCEDHORSES_REST, (byte) rest);
    }

    @Override
    public Container advancedhorses$getInventory() {
        return this.inventory;
    }

    @Override
    public HorseshoeContainer advancedhorses$getShoeContainer() {
        return this.advancedhorses$shoes;
    }

    @Inject(method = "dropEquipment", at = @At("TAIL"))
    private void advancedhorses$dropHorseshoes(ServerLevel level, CallbackInfo ci) {
        ItemStack shoes = this.advancedhorses$shoes.removeItemNoUpdate(0);
        if (!shoes.isEmpty()) {
            this.advancedhorses$self().spawnAtLocation(level, shoes);
        }
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void advancedhorses$defineSynchedData(SynchedEntityData.Builder entityData, CallbackInfo ci) {
        entityData.define(ADVANCEDHORSES_NEEDS, 0);
        entityData.define(ADVANCEDHORSES_PROGRESS, 0);
        entityData.define(ADVANCEDHORSES_GEAR, (byte) 0);
        entityData.define(ADVANCEDHORSES_REST, (byte) 0);
        for (HorseStat stat : HorseStat.values()) {
            entityData.define(ADVANCEDHORSES_STATS[stat.ordinal()], stat.defaultValue());
        }
        for (HorseByte value : HorseByte.values()) {
            entityData.define(ADVANCEDHORSES_BYTES[value.ordinal()], (byte) value.defaultValue());
        }
        entityData.define(ADVANCEDHORSES_PEDIGREE, "");
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void advancedhorses$tick(CallbackInfo ci) {
        AbstractHorse horse = this.advancedhorses$self();
        if (horse.level() instanceof ServerLevel level) {
            TamingManager.serverTick(horse, level);
            HorseLife.serverTick(horse, level);
        } else {
            this.advancedhorses$animations.clientTick(horse);
        }
    }

    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void advancedhorses$hurtServer(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            TamingManager.onHurt(this.advancedhorses$self(), source);
            HorseLife.onHurt(this.advancedhorses$self());
        }
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"), cancellable = true)
    private void advancedhorses$handleEntityEvent(byte id, CallbackInfo ci) {
        HorseAnimation animation = HorseAnimation.byEventId(id);
        if (animation != null) {
            this.advancedhorses$animations.start(animation, this.advancedhorses$self());
            ci.cancel();
        }
    }

    @Inject(method = "tameWithName", at = @At("TAIL"))
    private void advancedhorses$tamed(Player player, CallbackInfoReturnable<Boolean> cir) {
        AbstractHorse horse = this.advancedhorses$self();
        if (!horse.level().isClientSide()) {
            HorseLife.setHunger(horse, this.advancedhorses$lifeState, 80.0F);
        }
    }

    @Inject(method = "getInventoryColumns", at = @At("RETURN"), cancellable = true)
    private void advancedhorses$getInventoryColumns(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof Horse) {
            cir.setReturnValue(HorseStats.MAX_SLOTS / 3);
        }
    }

    @Inject(method = "isImmobile", at = @At("RETURN"), cancellable = true)
    private void advancedhorses$isImmobile(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && this.advancedhorses$getRest() != HorseRest.NONE) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "doPlayerRide", at = @At("HEAD"))
    private void advancedhorses$doPlayerRide(Player player, CallbackInfo ci) {
        if (!this.advancedhorses$self().level().isClientSide()) {
            HorseLife.standUp(this.advancedhorses$self(), false);
        }
    }

    @Inject(method = "handleEating", at = @At("RETURN"), cancellable = true)
    private void advancedhorses$handleEating(Player player, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        AbstractHorse horse = this.advancedhorses$self();
        if (!horse.isTamed() || !TamingManager.isTameableType(horse)) {
            return;
        }

        float value = HorseStats.foodValue(itemStack);
        if (horse.level().isClientSide()) {
            if (value > 0.0F && (HorseStats.get(horse, HorseStat.HUNGER) < HorseStats.MAX_HUNGER - 1.0F
                || HorseStats.hasFlag(horse, HorseByte.FLAG_LAME))) {
                cir.setReturnValue(true);
            }
            return;
        }

        if (HorseLife.onFed(horse, itemStack, value) && !cir.getReturnValueZ()) {
            ((AbstractHorseInvoker) (Object) this).advancedhorses$invokeEating();
            horse.gameEvent(GameEvent.EAT);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "setOffspringAttributes", at = @At("TAIL"))
    private void advancedhorses$inheritStats(AgeableMob partner, AbstractHorse baby, CallbackInfo ci) {
        HorseStats.inherit(this.advancedhorses$self(), partner, baby);
    }

    @Inject(method = "getRiddenSpeed", at = @At("RETURN"), cancellable = true)
    private void advancedhorses$getRiddenSpeed(Player controller, CallbackInfoReturnable<Float> cir) {
        AbstractHorse horse = this.advancedhorses$self();
        if (!TamingManager.isTameableType(horse)) {
            return;
        }
        HorseGait gait = HorseGait.current(horse);
        float factor = gait == HorseGait.STAND ? 1.0F : gait.speedFactor();
        float terrain = HorseStats.get(horse, HorseByte.TERRAIN) / 100.0F;
        cir.setReturnValue(cir.getReturnValueF() * factor * HorseStats.speedFactor(horse) * terrain);
    }

    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void advancedhorses$playStepSound(BlockPos pos, BlockState blockState, CallbackInfo ci) {
        if (TamingManager.isTameableType(this.advancedhorses$self()) && !blockState.liquid()) {
            ci.cancel();
        }
    }

    @Inject(method = "causeFallDamage", at = @At("RETURN"))
    private void advancedhorses$causeFallDamage(double fallDistance, float damageModifier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            HorseLife.onFall(this.advancedhorses$self(), fallDistance);
        }
    }

    @Inject(method = "getRiddenInput", at = @At("RETURN"), cancellable = true)
    private void advancedhorses$getRiddenInput(Player controller, Vec3 selfInput, CallbackInfoReturnable<Vec3> cir) {
        AbstractHorse horse = this.advancedhorses$self();
        Vec3 input = cir.getReturnValue();
        if (!TamingManager.isTameableType(horse) || input == Vec3.ZERO) {
            return;
        }
        if (HorseGait.current(horse) != HorseGait.STAND) {
            cir.setReturnValue(new Vec3(input.x, input.y, 1.0));
        } else if (input.z > 0.0) {
            cir.setReturnValue(new Vec3(input.x, input.y, 0.0));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void advancedhorses$save(ValueOutput output, CallbackInfo ci) {
        AbstractHorse horse = this.advancedhorses$self();
        for (HorseStat stat : HorseStat.values()) {
            output.putFloat(stat.saveKey(), HorseLife.exactStat(horse, stat));
        }
        for (HorseByte value : HorseByte.values()) {
            output.putInt(value.saveKey(), this.advancedhorses$getByte(value));
        }
        output.putString("AdvancedHorsesPedigree", this.advancedhorses$getPedigree());
        output.putLong("AdvancedHorsesBirth", this.advancedhorses$lifeState.birthTime);
        output.putInt("AdvancedHorsesLameTicks", this.advancedhorses$lifeState.lameTicks);
        output.putFloat("AdvancedHorsesShoeWear", this.advancedhorses$lifeState.shoeWear);
        ItemStack shoes = this.advancedhorses$shoes.getItem(0);
        if (!shoes.isEmpty()) {
            output.store("AdvancedHorsesShoe", ItemStack.CODEC, shoes);
        }
        output.putInt("AdvancedHorsesRest", this.advancedhorses$getRest());
        output.putInt("AdvancedHorsesRestTicks", this.advancedhorses$lifeState.restTicks);

        if (horse instanceof Horse) {
            ValueOutput.TypedOutputList<ItemStackWithSlot> items = output.list("AdvancedHorsesItems", ItemStackWithSlot.CODEC);
            for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                ItemStack stack = this.inventory.getItem(i);
                if (!stack.isEmpty()) {
                    items.add(new ItemStackWithSlot(i, stack));
                }
            }
        }

        int needs = this.advancedhorses$getSyncedNeeds();
        if (needs == 0) {
            return;
        }
        output.putInt("AdvancedHorsesNeeds", needs);
        output.putLong("AdvancedHorsesLastProgress", this.advancedhorses$tamingState.lastProgressTime);
        for (int slot = 0; slot < TamingData.SLOTS; slot++) {
            output.putFloat("AdvancedHorsesPoints" + slot, this.advancedhorses$tamingState.points[slot]);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void advancedhorses$load(ValueInput input, CallbackInfo ci) {
        AbstractHorse horse = this.advancedhorses$self();
        for (HorseStat stat : HorseStat.values()) {
            this.advancedhorses$setStat(stat, input.getFloatOr(stat.saveKey(), stat.defaultValue()));
        }
        for (HorseByte value : HorseByte.values()) {
            this.advancedhorses$setByte(value, input.getIntOr(value.saveKey(), value.defaultValue()));
        }
        this.advancedhorses$setPedigree(input.getStringOr("AdvancedHorsesPedigree", ""));
        this.advancedhorses$lifeState.birthTime = input.getLongOr("AdvancedHorsesBirth", -1L);
        this.advancedhorses$lifeState.lameTicks = input.getIntOr("AdvancedHorsesLameTicks", 0);
        ItemStack shoes = input.read("AdvancedHorsesShoe", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.advancedhorses$setByte(HorseByte.SHOES, Horseshoe.of(shoes).id());
        this.advancedhorses$shoes.setItem(0, shoes);
        this.advancedhorses$lifeState.shoeWear = input.getFloatOr("AdvancedHorsesShoeWear", 0.0F);
        if (input.getIntOr(HorseByte.LEVEL.saveKey(), -1) < 0) {
            float trained = 0.0F;
            for (String key : new String[]{"TRAINED_SPEED", "TRAINED_STAMINA", "TRAINED_STRENGTH"}) {
                trained += Math.max(0.0F, input.getFloatOr("AdvancedHorses" + key, 0.0F));
            }
            this.advancedhorses$setByte(HorseByte.LEVEL, Math.round(Math.min(1.0F, trained / 3.0F) * LEGACY_TRAINING_LEVEL));
        }
        HorseLife.loadState(horse);
        this.advancedhorses$setRest(input.getIntOr("AdvancedHorsesRest", HorseRest.NONE));
        this.advancedhorses$lifeState.restTicks = input.getIntOr("AdvancedHorsesRestTicks", 0);

        if (horse instanceof Horse) {
            for (ItemStackWithSlot item : input.listOrEmpty("AdvancedHorsesItems", ItemStackWithSlot.CODEC)) {
                if (item.isValidInContainer(this.inventory.getContainerSize())) {
                    this.inventory.setItem(item.slot(), item.stack());
                }
            }
        }

        TamingState state = this.advancedhorses$tamingState;
        state.reset();
        int needs = input.getIntOr("AdvancedHorsesNeeds", 0);
        this.advancedhorses$setSyncedNeeds(needs);
        if (needs == 0) {
            this.advancedhorses$setSyncedProgress(0);
            return;
        }

        state.lastProgressTime = input.getLongOr("AdvancedHorsesLastProgress", 0L);
        float[] fractions = new float[TamingData.SLOTS];
        for (int slot = 0; slot < TamingData.SLOTS; slot++) {
            state.points[slot] = input.getFloatOr("AdvancedHorsesPoints" + slot, 0.0F);
            var need = TamingData.need(needs, slot);
            fractions[slot] = need == null ? 0.0F : state.points[slot] / TamingManager.required(horse, need);
        }
        this.advancedhorses$setSyncedProgress(TamingData.packProgress(fractions));
    }
}
