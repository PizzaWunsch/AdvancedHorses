package dev.pizzawunsch.advancedhorses.horse;

import dev.pizzawunsch.advancedhorses.access.AdvancedHorseAccess;
import dev.pizzawunsch.advancedhorses.anim.HorseAnimation;
import dev.pizzawunsch.advancedhorses.block.TroughBlock;
import dev.pizzawunsch.advancedhorses.book.HorseRegistry;
import dev.pizzawunsch.advancedhorses.registry.ModEffects;
import dev.pizzawunsch.advancedhorses.registry.ModRules;
import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class HorseLife {
    private static final float IDLE_HUNGER_PER_TICK = HorseStats.MAX_HUNGER / 48000.0F;
    private static final float WANDER_HUNGER_PER_BLOCK = 0.004F;
    private static final float GRAZE_PER_TICK = 0.1F;
    private static final int LAME_TICKS = 6000;
    private static final float WINDED_RECOVERY = 30.0F;
    private static final float MIN_JUMP_HEIGHT = 0.6F;
    private static final float MAX_JUMP_HEIGHT = 6.0F;
    private static final float XP_PER_JUMP_BLOCK = 4.0F;

    private HorseLife() {
    }

    private static LifeState state(AbstractHorse horse) {
        return TamingManager.access(horse).advancedhorses$getLifeState();
    }

    public static void serverTick(AbstractHorse horse, ServerLevel level) {
        if (!TamingManager.isTameableType(horse) || !horse.isAlive()) {
            return;
        }

        AdvancedHorseAccess access = TamingManager.access(horse);
        LifeState state = state(horse);
        HorseStats.ensureGenetics(horse);

        ServerPlayer rider = horse.getControllingPassenger() instanceof ServerPlayer player ? player : null;
        if (rider == null && access.advancedhorses$getGear() != 0) {
            access.advancedhorses$setGear(0);
        }

        double distance = 0.0;
        if (state.hasLastPos) {
            distance = Math.sqrt(Mth.square(horse.getX() - state.lastX) + Mth.square(horse.getZ() - state.lastZ));
            if (distance > 4.0) {
                distance = 0.0;
            }
        }
        state.lastX = horse.getX();
        state.lastZ = horse.getZ();
        state.hasLastPos = true;

        tickAge(horse, level, state);
        if (horse.isTamed()) {
            tickHunger(horse, level, state, distance, rider);
        }
        tickEnergy(horse, level, state, distance, rider);
        HorseLevel.applyAttributes(horse);
        if (rider != null) {
            tickJump(horse, level, state, rider);
            if (distance > 0.0) {
                addXp(horse, level, state, (float) distance * HorseGait.current(horse).xpPerBlock(), rider);
                tickShoes(horse, level, state, distance, rider);
                setBond(horse, state, state.bond + (float) distance / 8000.0F);
            }
            tickTerrain(horse, level, state, distance, rider);
        } else {
            state.airborne = false;
            state.jumpStartY = horse.getY();
            if (HorseStats.get(horse, HorseByte.TERRAIN) != 100) {
                HorseStats.set(horse, HorseByte.TERRAIN, 100);
            }
        }
        tickLameness(horse, level, access, state);
        tickRest(horse, level, access, state);
        tickCalled(horse, level, access, state);
        tickTrough(horse, level, access, state);
        tickGrazing(horse, level, access, state);

        if (horse.isTamed() && horse.tickCount % 100 == 0) {
            HorseRegistry.update(horse);
        }
    }

    public static float exactStat(AbstractHorse horse, HorseStat stat) {
        LifeState state = state(horse);
        return switch (stat) {
            case HUNGER -> state.hunger;
            case ENERGY -> state.energy;
            case BOND -> state.bond;
            case XP -> state.xp;
            default -> HorseStats.get(horse, stat);
        };
    }

    public static void loadState(AbstractHorse horse) {
        LifeState state = state(horse);
        state.xp = Math.max(0.0F, HorseStats.get(horse, HorseStat.XP));
        state.hunger = HorseStats.get(horse, HorseStat.HUNGER);
        state.energy = HorseStats.get(horse, HorseStat.ENERGY);
        state.bond = HorseStats.get(horse, HorseStat.BOND);
    }

    private static void tickAge(AbstractHorse horse, ServerLevel level, LifeState state) {
        long now = level.getGameTime();
        if (state.birthTime < 0L) {
            int years = horse.isBaby() ? 0 : 3 + horse.getRandom().nextInt(10);
            state.birthTime = now - years * HorseStats.TICKS_PER_YEAR;
        }
        HorseStats.setFlag(horse, HorseByte.FLAG_AGING, ModRules.enabled(level, ModRules.AGING));
        if (horse.tickCount % 600 == 1) {
            int years = (int) Mth.clamp((now - state.birthTime) / HorseStats.TICKS_PER_YEAR, 0L, 127L);
            if (HorseStats.get(horse, HorseByte.AGE) != years) {
                HorseStats.set(horse, HorseByte.AGE, years);
            }
        }
    }

    private static void tickHunger(AbstractHorse horse, ServerLevel level, LifeState state, double distance, ServerPlayer rider) {
        if (!ModRules.enabled(level, ModRules.HUNGER)) {
            if (state.hunger < HorseStats.MAX_HUNGER) {
                setHunger(horse, state, HorseStats.MAX_HUNGER);
            }
            return;
        }

        float perBlock = rider != null ? HorseGait.current(horse).hungerPerBlock() : WANDER_HUNGER_PER_BLOCK;
        float staminaFactor = 1.3F - 0.6F * HorseStats.stamina(horse);
        float rate = ModRules.factor(level, ModRules.HUNGER_RATE) * HorseStats.breed(horse).hunger() * HorseStats.trait(horse).hunger();
        float drain = (IDLE_HUNGER_PER_TICK + (float) distance * perBlock) * staminaFactor * rate;
        float gain = horse.isEating() ? GRAZE_PER_TICK : 0.0F;
        setHunger(horse, state, state.hunger - drain + gain);

        if (rider != null && state.hunger < 25.0F && !state.hungerWarned) {
            state.hungerWarned = true;
            rider.sendOverlayMessage(Component.translatable("advancedhorses.hungry").withStyle(ChatFormatting.YELLOW));
        } else if (state.hunger >= 30.0F) {
            state.hungerWarned = false;
        }
    }

    public static void setHunger(AbstractHorse horse, LifeState state, float hunger) {
        state.hunger = Mth.clamp(hunger, 0.0F, HorseStats.MAX_HUNGER);
        sync(horse, HorseStat.HUNGER, state.hunger, 0.5F, HorseStats.MAX_HUNGER);
    }

    private static void setEnergy(AbstractHorse horse, LifeState state, float energy) {
        state.energy = Mth.clamp(energy, 0.0F, HorseStats.MAX_ENERGY);
        sync(horse, HorseStat.ENERGY, state.energy, 0.5F, HorseStats.MAX_ENERGY);
    }

    public static void setBond(AbstractHorse horse, LifeState state, float bond) {
        state.bond = Mth.clamp(bond, 0.0F, 1.0F);
        sync(horse, HorseStat.BOND, state.bond, 0.002F, 1.0F);
    }

    private static void sync(AbstractHorse horse, HorseStat stat, float value, float threshold, float max) {
        float synced = HorseStats.get(horse, stat);
        boolean boundary = value == 0.0F || value == max;
        if (Math.abs(synced - value) >= threshold || boundary && synced != value) {
            HorseStats.set(horse, stat, value);
        }
    }

    public static boolean feed(AbstractHorse horse, float value) {
        LifeState state = state(horse);
        if (value <= 0.0F || state.hunger >= HorseStats.MAX_HUNGER - 1.0F || !ModRules.enabled(horse.level(), ModRules.HUNGER)) {
            return false;
        }
        setHunger(horse, state, state.hunger + value);
        return true;
    }

    public static boolean onFed(AbstractHorse horse, ItemStack stack, float value) {
        LifeState state = state(horse);
        boolean ate = false;
        boolean healing = stack.is(Items.GOLDEN_CARROT) || stack.is(Items.GOLDEN_APPLE) || stack.is(Items.ENCHANTED_GOLDEN_APPLE);
        if (healing && HorseStats.hasFlag(horse, HorseByte.FLAG_LAME)) {
            heal(horse, state);
            ate = true;
        }
        if (feed(horse, value * HorseStats.trait(horse).food())) {
            ate = true;
        }
        if (ate) {
            setBond(horse, state, state.bond + 0.01F);
        }
        return ate;
    }

    private static void tickEnergy(AbstractHorse horse, ServerLevel level, LifeState state, double distance, ServerPlayer rider) {
        if (!ModRules.enabled(level, ModRules.STAMINA)) {
            if (state.energy < HorseStats.MAX_ENERGY) {
                setEnergy(horse, state, HorseStats.MAX_ENERGY);
            }
            HorseStats.setFlag(horse, HorseByte.FLAG_WINDED, false);
            return;
        }

        boolean potion = horse.hasEffect(ModEffects.HORSE_STAMINA);
        HorseStats.setFlag(horse, HorseByte.FLAG_STAMINA_POTION, potion);
        HorseGait gait = rider != null ? HorseGait.current(horse) : HorseGait.WALK;
        boolean moving = distance > 0.05;
        float change;
        if (moving && gait.ordinal() >= HorseGait.CANTER.ordinal()) {
            float drain = switch (gait) {
                case CANTER -> 0.03F;
                case GALLOP -> 0.08F;
                default -> 0.35F;
            };
            change = potion ? 0.0F : -drain * (1.4F - 0.8F * HorseStats.stamina(horse)) * HorseStats.trait(horse).energy();
        } else {
            float regen = moving && gait == HorseGait.TROT ? 0.12F : 0.35F;
            change = regen * (1.0F + 0.3F * HorseStats.bond(horse));
        }
        setEnergy(horse, state, state.energy + change);

        boolean winded = HorseStats.hasFlag(horse, HorseByte.FLAG_WINDED);
        if (!winded && state.energy <= 0.0F) {
            HorseStats.setFlag(horse, HorseByte.FLAG_WINDED, true);
            if (rider != null) {
                rider.sendOverlayMessage(Component.translatable("advancedhorses.winded").withStyle(ChatFormatting.YELLOW));
                if (gait == HorseGait.RACE && horse.getRandom().nextFloat() < 0.2F * injuryFactor(horse)) {
                    makeLame(horse, rider);
                }
            }
            limitGear(horse, HorseGait.CANTER);
        } else if (winded && state.energy >= WINDED_RECOVERY) {
            HorseStats.setFlag(horse, HorseByte.FLAG_WINDED, false);
        }
    }

    private static void limitGear(AbstractHorse horse, HorseGait max) {
        AdvancedHorseAccess access = TamingManager.access(horse);
        if (access.advancedhorses$getGear() > max.ordinal()) {
            access.advancedhorses$setGear(max.ordinal());
        }
    }

    private static void tickJump(AbstractHorse horse, ServerLevel level, LifeState state, ServerPlayer rider) {
        if (horse.onGround() || horse.isInWater()) {
            if (state.airborne) {
                state.airborne = false;
                float height = (float) Math.min(MAX_JUMP_HEIGHT, state.jumpPeakY - state.jumpStartY);
                if (height >= MIN_JUMP_HEIGHT) {
                    addXp(horse, level, state, height * XP_PER_JUMP_BLOCK, rider);
                }
            }
            state.jumpStartY = horse.getY();
            return;
        }
        if (!state.airborne) {
            state.airborne = true;
            state.jumpPeakY = horse.getY();
        }
        state.jumpPeakY = Math.max(state.jumpPeakY, horse.getY());
    }

    private static void addXp(AbstractHorse horse, ServerLevel level, LifeState state, float amount, ServerPlayer rider) {
        int current = HorseLevel.level(horse);
        if (amount <= 0.0F || !horse.isTamed() || current >= HorseLevel.MAX_LEVEL) {
            return;
        }
        float rate = ModRules.factor(level, ModRules.XP_RATE) * HorseStats.trait(horse).xp()
            * (1.0F + 0.5F * HorseStats.bond(horse)) * HorseStats.ageXpFactor(horse)
            * (1.0F + carriedStacks(horse) / 12.0F);
        state.xp += amount * rate;

        while (current < HorseLevel.MAX_LEVEL && state.xp >= HorseLevel.xpForNext(current)) {
            state.xp -= HorseLevel.xpForNext(current);
            current++;
            HorseStats.set(horse, HorseByte.LEVEL, current);
            onLevelUp(horse, current, rider);
        }
        if (current >= HorseLevel.MAX_LEVEL) {
            state.xp = 0.0F;
        }
        sync(horse, HorseStat.XP, state.xp, 1.0F, HorseLevel.xpForNext(current));
    }

    private static void onLevelUp(AbstractHorse horse, int level, ServerPlayer rider) {
        HorseUpgrade upgrade = HorseUpgrade.forLevel(level);
        HorseLevel.applyAttributes(horse);
        if (upgrade == HorseUpgrade.HEALTH) {
            horse.heal(upgrade.perStep());
        }
        horse.playSound(SoundEvents.PLAYER_LEVELUP, 0.6F, 1.2F);
        TamingManager.playAnimation(horse, HorseAnimation.HEAD_TOSS);
        rider.sendOverlayMessage(Component.translatable("advancedhorses.level_up", horse.getName(), level,
            Component.translatable(upgrade.translationKey())).withStyle(ChatFormatting.AQUA));
    }

    private static int carriedStacks(AbstractHorse horse) {
        Container inventory = TamingManager.access(horse).advancedhorses$getInventory();
        int count = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (!inventory.getItem(i).isEmpty()) {
                count++;
            }
        }
        return Math.min(count, HorseStats.MAX_SLOTS);
    }

    private static void tickShoes(AbstractHorse horse, ServerLevel level, LifeState state, double distance, ServerPlayer rider) {
        Container container = TamingManager.access(horse).advancedhorses$getShoeContainer();
        ItemStack shoes = container.getItem(0);
        if (shoes.isEmpty() || !shoes.isDamageableItem()) {
            return;
        }
        state.shoeWear += (float) distance;
        int wear = Mth.floor(state.shoeWear);
        if (wear <= 0) {
            return;
        }
        state.shoeWear -= wear;
        int damage = shoes.getDamageValue() + EnchantmentHelper.processDurabilityChange(level, shoes, wear);
        if (damage >= shoes.getMaxDamage()) {
            container.setItem(0, ItemStack.EMPTY);
            rider.sendOverlayMessage(Component.translatable("advancedhorses.shoes.worn").withStyle(ChatFormatting.YELLOW));
            horse.playSound(SoundEvents.ITEM_BREAK.value(), 0.8F, 1.0F);
        } else {
            shoes.setDamageValue(damage);
        }
    }

    public static int repairShoesWithXp(ServerPlayer player, int amount) {
        if (amount <= 0 || !(player.getVehicle() instanceof AbstractHorse horse) || !TamingManager.isTameableType(horse)) {
            return amount;
        }
        ItemStack shoes = TamingManager.access(horse).advancedhorses$getShoeContainer().getItem(0);
        if (shoes.isEmpty() || !shoes.isDamaged()) {
            return amount;
        }
        int durability = EnchantmentHelper.modifyDurabilityToRepairFromXp(player.level(), shoes, amount);
        if (durability <= 0) {
            return amount;
        }
        int repaired = Math.min(durability, shoes.getDamageValue());
        shoes.setDamageValue(shoes.getDamageValue() - repaired);
        return amount - repaired * amount / durability;
    }

    public static boolean giveStaminaPotion(AbstractHorse horse, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (!stack.is(Items.POTION) || contents == null) {
            return false;
        }
        int duration = contents.is(ModEffects.LONG_STAMINA_POTION) ? ModEffects.LONG_DURATION
            : contents.is(ModEffects.STAMINA_POTION) ? ModEffects.DURATION : 0;
        if (duration == 0) {
            return false;
        }
        if (!player.level().isClientSide()) {
            horse.addEffect(new MobEffectInstance(ModEffects.HORSE_STAMINA, duration), player);
            horse.playSound(SoundEvents.GENERIC_DRINK.value(), 1.0F, 1.0F);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
        }
        return true;
    }

    private static void tickTerrain(AbstractHorse horse, ServerLevel level, LifeState state, double distance, ServerPlayer rider) {
        int percent = 100;
        if (ModRules.enabled(level, ModRules.TERRAIN_SPEED)) {
            Horseshoe shoes = HorseStats.shoes(horse);
            BlockState feet = level.getBlockState(horse.blockPosition());
            BlockState on = horse.getBlockStateOn();
            float factor;
            boolean hard = false;
            if (feet.is(Blocks.SNOW)) {
                factor = 0.85F;
            } else if (on.is(Blocks.DIRT_PATH)) {
                factor = 1.1F;
            } else if (on.is(Blocks.SAND) || on.is(Blocks.RED_SAND) || on.is(Blocks.SUSPICIOUS_SAND)) {
                factor = 0.8F;
            } else if (on.is(Blocks.SOUL_SAND) || on.is(Blocks.SOUL_SOIL)) {
                factor = 0.6F;
            } else if (on.is(Blocks.MUD)) {
                factor = 0.65F;
            } else if (on.is(Blocks.SNOW_BLOCK) || on.is(Blocks.POWDER_SNOW)) {
                factor = 0.8F;
            } else if (on.is(Blocks.GRAVEL)) {
                factor = 0.95F;
            } else if (isHard(on)) {
                hard = true;
                factor = 1.0F + shoes.hardGround();
            } else {
                factor = 1.0F;
            }
            percent = Math.round((factor + shoes.everywhere()) * 100.0F);

            if (hard && shoes == Horseshoe.NONE && HorseGait.current(horse).ordinal() >= HorseGait.GALLOP.ordinal()) {
                state.unshodHardDistance += distance;
                if (state.unshodHardDistance >= 100.0) {
                    state.unshodHardDistance = 0.0;
                    if (horse.getRandom().nextFloat() < 0.01F * injuryFactor(horse)) {
                        makeLame(horse, rider);
                    }
                }
            }
        }
        if (HorseStats.get(horse, HorseByte.TERRAIN) != percent) {
            HorseStats.set(horse, HorseByte.TERRAIN, percent);
        }
    }

    private static boolean isHard(BlockState state) {
        SoundType sound = state.getSoundType();
        return sound == SoundType.STONE || sound == SoundType.DEEPSLATE || sound == SoundType.DEEPSLATE_BRICKS
            || sound == SoundType.DEEPSLATE_TILES || sound == SoundType.POLISHED_DEEPSLATE || sound == SoundType.TUFF
            || sound == SoundType.NETHER_BRICKS || sound == SoundType.NETHERRACK;
    }

    private static float injuryFactor(AbstractHorse horse) {
        return HorseStats.trait(horse).injury() * HorseStats.shoes(horse).injury();
    }

    public static void onFall(AbstractHorse horse, double fallDistance) {
        if (fallDistance > 6.0 && horse.getRandom().nextFloat() < 0.4F * injuryFactor(horse)) {
            makeLame(horse, horse.getControllingPassenger() instanceof ServerPlayer player ? player : null);
        }
    }

    public static void makeLame(AbstractHorse horse, ServerPlayer notify) {
        if (!ModRules.enabled(horse.level(), ModRules.INJURIES) || HorseStats.hasFlag(horse, HorseByte.FLAG_LAME)) {
            return;
        }
        HorseStats.setFlag(horse, HorseByte.FLAG_LAME, true);
        state(horse).lameTicks = LAME_TICKS;
        limitGear(horse, HorseGait.TROT);
        horse.playSound(SoundEvents.HORSE_HURT, 1.0F, 0.8F);
        if (notify != null) {
            notify.sendOverlayMessage(Component.translatable("advancedhorses.lame").withStyle(ChatFormatting.RED));
        }
    }

    private static void heal(AbstractHorse horse, LifeState state) {
        HorseStats.setFlag(horse, HorseByte.FLAG_LAME, false);
        state.lameTicks = 0;
    }

    private static void tickLameness(AbstractHorse horse, ServerLevel level, AdvancedHorseAccess access, LifeState state) {
        if (!HorseStats.hasFlag(horse, HorseByte.FLAG_LAME)) {
            return;
        }
        if (!ModRules.enabled(level, ModRules.INJURIES)) {
            heal(horse, state);
            return;
        }
        state.lameTicks -= access.advancedhorses$getRest() != HorseRest.NONE ? 4 : 1;
        limitGear(horse, HorseGait.TROT);
        if (state.lameTicks <= 0) {
            heal(horse, state);
        }
    }

    private static void tickRest(AbstractHorse horse, ServerLevel level, AdvancedHorseAccess access, LifeState state) {
        if (horse.isVehicle()) {
            if (access.advancedhorses$getRest() != HorseRest.NONE) {
                standUp(horse, false);
            }
            return;
        }

        int rest = access.advancedhorses$getRest();
        if (horse.isTamed() && state.hunger <= 0.0F) {
            if (rest != HorseRest.EXHAUSTED) {
                access.advancedhorses$setRest(HorseRest.EXHAUSTED);
            }
        } else if (rest == HorseRest.EXHAUSTED) {
            access.advancedhorses$setRest(HorseRest.LYING);
            state.restTicks = 200;
        } else if (rest == HorseRest.LYING && (--state.restTicks <= 0 || state.calledBy != null)) {
            standUp(horse, true);
        }

        if (access.advancedhorses$getRest() != HorseRest.NONE) {
            horse.getNavigation().stop();
            if (horse.isStanding()) {
                horse.clearStanding();
            }
            return;
        }

        if (!ModRules.enabled(level, ModRules.RESTING) || state.calledBy != null || state.trough != null
            || !horse.onGround() || horse.isInWater() || horse.isEating() || horse.isStanding()) {
            return;
        }

        RandomSource random = horse.getRandom();
        boolean weak = horse.isTamed() && state.hunger < 20.0F;
        boolean hurt = HorseStats.hasFlag(horse, HorseByte.FLAG_LAME);
        int chance = weak ? 300 : hurt ? 800 : level.isDarkOutside() ? 3000 : 12000;
        if (random.nextInt(chance) == 0) {
            access.advancedhorses$setRest(HorseRest.LYING);
            state.restTicks = weak ? 800 + random.nextInt(800) : 400 + random.nextInt(800);
        }
    }

    private static void tickGrazing(AbstractHorse horse, ServerLevel level, AdvancedHorseAccess access, LifeState state) {
        if (!horse.isTamed() || horse.isVehicle() || horse.isEating() || !horse.onGround() || state.trough != null
            || access.advancedhorses$getRest() != HorseRest.NONE || state.hunger >= 70.0F) {
            return;
        }
        if (horse.getRandom().nextInt(state.hunger < 30.0F ? 60 : 200) == 0
            && level.getBlockState(horse.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
            horse.setEating(true);
        }
    }

    private static void tickTrough(AbstractHorse horse, ServerLevel level, AdvancedHorseAccess access, LifeState state) {
        if (!horse.isTamed() || horse.isVehicle() || horse.isLeashed() || state.calledBy != null
            || access.advancedhorses$getRest() != HorseRest.NONE || state.hunger >= 60.0F
            || !ModRules.enabled(level, ModRules.HUNGER)) {
            state.trough = null;
            return;
        }
        long now = level.getGameTime();
        if (now < state.troughCooldownUntil || horse.tickCount % 20 != 0) {
            return;
        }

        if (state.trough == null || !TroughBlock.hasFood(level.getBlockState(state.trough))) {
            state.trough = findTrough(horse, level);
            if (state.trough == null) {
                state.troughCooldownUntil = now + 100L;
                return;
            }
        }

        Vec3 center = Vec3.atBottomCenterOf(state.trough);
        if (horse.position().distanceToSqr(center) > 2.6 * 2.6) {
            horse.getNavigation().moveTo(center.x, center.y, center.z, 1.0);
            return;
        }

        horse.getNavigation().stop();
        horse.getLookControl().setLookAt(center.x, center.y, center.z);
        if (TroughBlock.eatFrom(level, state.trough)) {
            setHunger(horse, state, state.hunger + TroughBlock.HUNGER_PER_LEVEL * HorseStats.trait(horse).food());
            horse.setEating(true);
            level.playSound(null, horse.getX(), horse.getY(), horse.getZ(), SoundEvents.HORSE_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
        state.trough = null;
        state.troughCooldownUntil = now + 200L;
    }

    private static BlockPos findTrough(AbstractHorse horse, ServerLevel level) {
        BlockPos origin = horse.blockPosition();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-10, -3, -10), origin.offset(10, 3, 10))) {
            if (TroughBlock.hasFood(level.getBlockState(pos))) {
                double d = pos.distSqr(origin);
                if (d < bestDistance) {
                    bestDistance = d;
                    best = pos.immutable();
                }
            }
        }
        return best;
    }

    public static void whistle(ServerPlayer player) {
        ServerLevel level = player.level();
        long now = level.getGameTime();
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_FLUTE.value(), SoundSource.PLAYERS, 1.0F, 1.8F);

        AbstractHorse best = null;
        double bestDistance = Double.MAX_VALUE;
        for (AbstractHorse horse : level.getEntitiesOfClass(AbstractHorse.class, player.getBoundingBox().inflate(128.0),
            horse -> horse.isTamed() && TamingManager.isTameableType(horse) && isOwner(horse, player) && !horse.isVehicle())) {
            double range = 48.0 + 80.0 * HorseStats.bond(horse);
            double d = horse.distanceToSqr(player);
            if (d <= range * range && d < bestDistance) {
                bestDistance = d;
                best = horse;
            }
        }

        if (best == null) {
            player.sendOverlayMessage(Component.translatable("advancedhorses.whistle.none").withStyle(ChatFormatting.GRAY));
            return;
        }
        if (TamingManager.access(best).advancedhorses$getRest() == HorseRest.EXHAUSTED) {
            player.sendOverlayMessage(Component.translatable("advancedhorses.exhausted").withStyle(ChatFormatting.RED));
            return;
        }

        LifeState state = state(best);
        state.calledBy = player.getUUID();
        state.calledUntil = now + 600L;
        state.trough = null;
        if (best.isLeashed()) {
            best.dropLeash();
        }
        best.playSound(SoundEvents.HORSE_AMBIENT, 1.2F, 1.0F);
        TamingManager.playAnimation(best, HorseAnimation.HEAD_TOSS);
        player.sendOverlayMessage(Component.translatable("advancedhorses.whistle.coming", best.getName()).withStyle(ChatFormatting.GREEN));
    }

    private static void tickCalled(AbstractHorse horse, ServerLevel level, AdvancedHorseAccess access, LifeState state) {
        if (state.calledBy == null) {
            return;
        }
        Player player = level.getPlayerByUUID(state.calledBy);
        if (player == null || horse.isVehicle() || level.getGameTime() > state.calledUntil) {
            state.calledBy = null;
            return;
        }
        double distance = horse.distanceTo(player);
        if (distance < 3.0) {
            state.calledBy = null;
            horse.getNavigation().stop();
            horse.getLookControl().setLookAt(player);
            return;
        }
        if (horse.tickCount % 10 != 0) {
            return;
        }
        Vec3 target = player.position();
        if (distance > 14.0) {
            Vec3 step = LandRandomPos.getPosTowards(horse, 14, 4, target);
            if (step != null) {
                target = step;
            }
        }
        horse.getNavigation().moveTo(target.x, target.y, target.z, distance > 10.0 ? 1.8 : 1.2);
    }

    public static boolean groom(AbstractHorse horse, ServerPlayer player) {
        LifeState state = state(horse);
        long now = horse.level().getGameTime();
        if (now < state.groomCooldownUntil) {
            return false;
        }
        state.groomCooldownUntil = now + 400L;
        setBond(horse, state, state.bond + 0.015F);
        int percent = Math.round(state.bond * 100.0F);
        player.sendOverlayMessage(Component.translatable("advancedhorses.bond", percent).withStyle(ChatFormatting.LIGHT_PURPLE));
        return true;
    }

    public static void standUp(AbstractHorse horse, boolean stretch) {
        AdvancedHorseAccess access = TamingManager.access(horse);
        if (access.advancedhorses$getRest() == HorseRest.NONE) {
            return;
        }
        access.advancedhorses$setRest(HorseRest.NONE);
        state(horse).restTicks = 0;
        if (stretch) {
            TamingManager.playAnimation(horse, HorseAnimation.STRETCH);
        }
    }

    public static void onHurt(AbstractHorse horse) {
        if (TamingManager.access(horse).advancedhorses$getRest() == HorseRest.LYING) {
            standUp(horse, false);
        }
    }

    public static void onGaitChanged(AbstractHorse horse, HorseGait from, HorseGait to) {
        if (from.ordinal() >= HorseGait.GALLOP.ordinal() && to.ordinal() <= HorseGait.TROT.ordinal()) {
            TamingManager.playAnimation(horse, HorseAnimation.HEAD_SHAKE);
        } else if (to == HorseGait.RACE && from != HorseGait.RACE) {
            TamingManager.playAnimation(horse, HorseAnimation.HEAD_TOSS);
        }
    }

    public static UUID ownerId(AbstractHorse horse) {
        var reference = horse.getOwnerReference();
        return reference == null ? null : reference.getUUID();
    }

    public static boolean isOwner(AbstractHorse horse, LivingEntity entity) {
        UUID owner = ownerId(horse);
        return owner != null && owner.equals(entity.getUUID());
    }
}
