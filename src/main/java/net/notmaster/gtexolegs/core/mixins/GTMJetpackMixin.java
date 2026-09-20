package net.notmaster.gtexolegs.core.mixins;

import com.gregtechceu.gtceu.api.item.armor.ArmorUtils;
import com.gregtechceu.gtceu.common.item.armor.IJetpack;
import com.gregtechceu.gtceu.utils.input.KeyBind;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import net.notmaster.gtexolegs.common.item.ItemExoskeletonLegs;
import net.notmaster.gtexolegs.config.GTExoLegsConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.OptionalInt;

@SuppressWarnings("unused")
@Mixin(value = IJetpack.class, remap = false)
public interface GTMJetpackMixin {

    @Shadow(remap = false)
    double getSprintEnergyModifier();

    @Shadow(remap = false)
    double getSprintSpeedModifier();

    @Shadow(remap = false)
    double getVerticalHoverSpeed();

    @Shadow(remap = false)
    double getVerticalHoverSlowSpeed();

    @Shadow(remap = false)
    double getVerticalAcceleration();

    @Shadow(remap = false)
    double getVerticalSpeed();

    @Shadow(remap = false)
    double getSidewaysSpeed();

    @Shadow(remap = false)
    @Nullable
    ParticleOptions getParticle();

    @Shadow(remap = false)
    float getFallDamageReduction();

    @Shadow(remap = false)
    int getEnergyPerUse();

    @Shadow(remap = false)
    boolean canUseEnergy(ItemStack stack, int amount);

    @Shadow(remap = false)
    void drainEnergy(ItemStack stack, int amount);

    @Shadow(remap = false)
    boolean hasEnergy(ItemStack stack);

    /**
     * @author NOT_MASTER08
     * @reason Can't use injectors into interfaces
     *         Code from <a href=
     *         "https://github.com/GregTechCEu/GregTech-Modern/blob/1.20.1/src/main/java/com/gregtechceu/gtceu/common/item/armor/IJetpack.java#L74">GTM</a>
     */
    @Overwrite(remap = false)
    default void performFlying(@NotNull Player player, boolean flightEnabled, boolean hover, ItemStack stack) {
        double deltaY = player.getDeltaMovement().y();

        if ((!flightEnabled || !hover) && player.getY() < player.level().getMinBuildHeight() - 5) {
            performEHover(stack, player);
        } else if (!flightEnabled) {
            return;
        }

        boolean flyKeyDown = KeyBind.VANILLA_JUMP.isKeyDown(player);
        boolean descendKeyDown = KeyBind.VANILLA_SNEAK.isKeyDown(player);
        double currentAccel = gtexolegs$modifiedVerticalAcceleration(player) * (deltaY < 0.3D ? 2.5D : 1.0D);

        if (!player.onGround() && player.getSleepingPos().isEmpty() && canUseEnergy(stack, getEnergyPerUse())) {
            double potentialY = 0;
            boolean editMotion = true;

            if (hover) {
                if (flyKeyDown && descendKeyDown) {
                    potentialY = gtexolegs$modifiedVertialHoverSlowSpeed(player);
                } else if (flyKeyDown) {
                    potentialY = gtexolegs$modifiedVerticalHoverSpeed(player);
                } else if (descendKeyDown) {
                    potentialY = -gtexolegs$modifiedVerticalHoverSpeed(player);
                } else {
                    potentialY = -gtexolegs$modifiedVertialHoverSlowSpeed(player);
                }

                if (player.isFallFlying()) { // if the player is hovering negate fall motion
                    player.stopFallFlying();
                }
            } else {
                if (flyKeyDown && descendKeyDown) {
                    potentialY = 0;
                } else if (flyKeyDown) {
                    potentialY = gtexolegs$modifiedVerticalSpeed(player) * (player.isInWater() ? 0.4D : 1.0D);
                } else { // Free fall, don't need to edit motion
                    editMotion = false;
                }
            }

            if (editMotion) {
                potentialY = Math.min(deltaY + currentAccel, potentialY);
                var motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.x(), potentialY, motion.z());
            }

            float speedSideways = (float) (player.isShiftKeyDown() ? gtexolegs$modifiedSidewaysSpeed(player) * 0.5f :
                    gtexolegs$modifiedSidewaysSpeed(player));
            float speedForward = (float) (player.isSprinting() ? speedSideways * getSprintSpeedModifier() :
                    speedSideways);

            // Make sure they aren't using elytra movement
            if (!player.isFallFlying()) {
                Vec3 movement = new Vec3(0, 0, 0);
                if (KeyBind.VANILLA_FORWARD.isKeyDown(player)) movement = movement.add(0, 0, speedForward);
                if (KeyBind.VANILLA_BACKWARD.isKeyDown(player)) movement = movement.add(0, 0, -speedSideways * 0.8f);
                if (KeyBind.VANILLA_LEFT.isKeyDown(player)) movement = movement.add(speedSideways, 0, 0);
                if (KeyBind.VANILLA_RIGHT.isKeyDown(player)) movement = movement.add(-speedSideways, 0, 0);

                var dist = movement.length();
                if (dist >= 1.0E-7) {
                    player.moveRelative((float) dist, movement);
                    if (!editMotion) editMotion = true;
                }
            }

            if (editMotion) {
                int energyUsed = (int) Math
                        .round(getEnergyPerUse() * (player.isSprinting() ? getSprintEnergyModifier() : 1));
                drainEnergy(stack, energyUsed);
                ArmorUtils.spawnParticle(player.level(), player, getParticle(), -0.6D);
            }

            // ensure that the player is actually using the jetpack to cancel fall damage
            if (!player.level().isClientSide && (hover || flyKeyDown)) {
                player.fallDistance = 0;
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.aboveGroundTickCount = 0;
                }
            }

        }
    }

    @Shadow(remap = false)
    static void performEHover(ItemStack stack, Player player) {}

    @Unique
    default OptionalInt gtexolegs$findEquippedTier(Player player) {
        return CuriosApi.getCuriosInventory(player).map(curios -> {
            for(var result : curios.findCurios("exoskeleton_legs")) {
                if(result.stack().getItem() instanceof ItemExoskeletonLegs legs) {
                    if(ItemExoskeletonLegs.isFeatureEnabled(result.stack(), ItemExoskeletonLegs.Features.FLIGHT_MODIFIER)) {
                        return OptionalInt.of(legs.tier);
                    }
                }
            }
            return OptionalInt.empty();
        }).orElse(OptionalInt.empty());
    }

    @Unique
    default double gtexolegs$modifiedVertialHoverSlowSpeed(Player player) {
        double multiplier = 1.0;
        OptionalInt tier = gtexolegs$findEquippedTier(player);
        if(tier.isPresent()) {
            multiplier = GTExoLegsConfig.jetpack.verticalHoverSlowMultiplier[tier.getAsInt() -1];
        }
        return getVerticalHoverSlowSpeed() * multiplier;
    }

    @Unique
    default double gtexolegs$modifiedVerticalHoverSpeed(Player player) {
        double multiplier = 1.0;
        OptionalInt tier = gtexolegs$findEquippedTier(player);
        if(tier.isPresent()) {
            multiplier = GTExoLegsConfig.jetpack.verticalHoverMultiplier[tier.getAsInt() -1];
        }
        return getVerticalHoverSpeed() * multiplier;
    }

    @Unique
    default double gtexolegs$modifiedVerticalAcceleration(Player player) {
        double multiplier = 1.0;
        OptionalInt tier = gtexolegs$findEquippedTier(player);
        if(tier.isPresent()) {
            multiplier = GTExoLegsConfig.jetpack.verticalAccelerationMultiplier[tier.getAsInt() -1];
        }
        return getVerticalAcceleration() * multiplier;
    }

    @Unique
    default double gtexolegs$modifiedVerticalSpeed(Player player) {
        double multiplier = 1.0;
        OptionalInt tier = gtexolegs$findEquippedTier(player);
        if(tier.isPresent()) {
            multiplier = GTExoLegsConfig.jetpack.verticalSpeedMultiplier[tier.getAsInt() -1];
        }
        return getVerticalSpeed() * multiplier;
    }

    @Unique
    default double gtexolegs$modifiedSidewaysSpeed(Player player) {
        double multiplier = 1.0;
        OptionalInt tier = gtexolegs$findEquippedTier(player);
        if(tier.isPresent()) {
            multiplier = GTExoLegsConfig.jetpack.horizontalSpeedMultiplier[tier.getAsInt() -1];
        }
        return getSidewaysSpeed() * multiplier;
    }
}
