package net.notmaster.gtexolegs.core.mixins.compat.ironjetpacks;

import net.minecraft.world.entity.player.Player;
import net.notmaster.gtexolegs.common.item.ItemExoskeletonLegs;
import net.notmaster.gtexolegs.config.GTExoLegsConfig;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(JetpackItem.class)
public abstract class IronJetpackMixin {

    @ModifyExpressionValue(
                           method = "onArmorTick",
                           remap = false,
                           at = @At(
                                    value = "FIELD",
                                    target = "Lcom/blakebr0/ironjetpacks/registry/Jetpack;speedHoverSlow:D",
                                    opcode = Opcodes.GETFIELD,
                                    remap = false))
    private double gtexolegs$modifySpeedHoverSlow(double original, @Local(argsOnly = true) Player player) {
        var tier = ItemExoskeletonLegs.findEquippedTier(player);
        if (tier.isPresent()) {
            return original * GTExoLegsConfig.INSTANCE.jetpack.verticalHoverSlowMultiplier[tier.getAsInt() - 1];
        }
        return original;
    }

    @ModifyExpressionValue(
                           method = "onArmorTick",
                           remap = false,
                           at = @At(
                                    value = "FIELD",
                                    target = "Lcom/blakebr0/ironjetpacks/registry/Jetpack;speedHoverAscend:D",
                                    opcode = Opcodes.GETFIELD,
                                    remap = false))
    private double gtexolegs$modifySpeedHoverAscend(double original, @Local(argsOnly = true) Player player) {
        var tier = ItemExoskeletonLegs.findEquippedTier(player);
        if (tier.isPresent()) {
            return original * GTExoLegsConfig.INSTANCE.jetpack.verticalHoverMultiplier[tier.getAsInt() - 1];
        }
        return original;
    }

    @ModifyExpressionValue(
                           method = "onArmorTick",
                           remap = false,
                           at = @At(
                                    value = "FIELD",
                                    target = "Lcom/blakebr0/ironjetpacks/registry/Jetpack;speedHoverDescend:D",
                                    opcode = Opcodes.GETFIELD,
                                    remap = false))
    private double gtexolegs$modifySpeedHoverDescend(double original, @Local(argsOnly = true) Player player) {
        var tier = ItemExoskeletonLegs.findEquippedTier(player);
        if (tier.isPresent()) {
            return original * GTExoLegsConfig.INSTANCE.jetpack.verticalHoverMultiplier[tier.getAsInt() - 1];
        }
        return original;
    }

    @ModifyExpressionValue(
                           method = "onArmorTick",
                           remap = false,
                           at = @At(
                                    value = "FIELD",
                                    target = "Lcom/blakebr0/ironjetpacks/registry/Jetpack;accelVert:D",
                                    opcode = Opcodes.GETFIELD,
                                    remap = false))
    private double gtexolegs$modifyAccelVert(double original, @Local(argsOnly = true) Player player) {
        var tier = ItemExoskeletonLegs.findEquippedTier(player);
        if (tier.isPresent()) {
            return original * GTExoLegsConfig.INSTANCE.jetpack.verticalAccelerationMultiplier[tier.getAsInt() - 1];
        }
        return original;
    }

    @ModifyExpressionValue(
                           method = "onArmorTick",
                           remap = false,
                           at = @At(
                                    value = "FIELD",
                                    target = "Lcom/blakebr0/ironjetpacks/registry/Jetpack;speedSide:D",
                                    opcode = Opcodes.GETFIELD,
                                    remap = false))
    private double gtexolegs$modifySpeedSide(double original, @Local(argsOnly = true) Player player) {
        var tier = ItemExoskeletonLegs.findEquippedTier(player);
        if (tier.isPresent()) {
            return original * GTExoLegsConfig.INSTANCE.jetpack.horizontalSpeedMultiplier[tier.getAsInt() - 1];
        }
        return original;
    }
}
