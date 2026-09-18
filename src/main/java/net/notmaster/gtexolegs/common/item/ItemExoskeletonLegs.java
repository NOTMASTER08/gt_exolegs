package net.notmaster.gtexolegs.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.notmaster.gtexolegs.common.entity.attribute.GTExoLegsAttributes;
import net.notmaster.gtexolegs.config.GTExoLegsConfig;

import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class ItemExoskeletonLegs extends Item implements ICurioItem {

    private static final UUID STEP_HEIGHT_MODIFIER_UUID = UUID.fromString("b8a6abba-b241-4795-9ff9-00d98b52ec04");
    private static final UUID MOVEMENT_SPEED_MULTIPLIER_UUID = UUID.fromString("0687d81a-0ef8-4c45-9a5a-a1e6a13719a9");
    private static final UUID JUMP_BOOST_MODIFIER_UUID = UUID.fromString("dfc1fb10-e6a6-4598-902c-8f22b0195b38");
    private static final String JUMP_BOOST_ENABLED_TAG = "jumpBoostEnabled";

    private final int tier;

    protected ItemExoskeletonLegs(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    private void setAttributeModifier(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            if (instance.getModifier(modifier.getId()) != null) {
                instance.removeModifier(modifier.getId());
            }
            instance.addTransientModifier(modifier);
        }
    }

    private void removeAttributeModifier(LivingEntity entity, Attribute attribute, UUID id) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            if (instance.getModifier(id) != null) {
                instance.removeModifier(id);
            }
        }
    }

    private void setJumpBoost(LivingEntity entity) {
        if (!GTExoLegsConfig.INSTANCE.enableJumpBoost) return;
        setAttributeModifier(entity, GTExoLegsAttributes.JUMP_BOOST_ADDITION.get(),
                new AttributeModifier(
                        JUMP_BOOST_MODIFIER_UUID,
                        "Exoskeleton Legs Jump Bost Modifier",
                        GTExoLegsConfig.INSTANCE.jumpBoostModifiers[tier - 1],
                        AttributeModifier.Operation.ADDITION));
    }

    private void removeJumpBoost(LivingEntity entity) {
        if (GTExoLegsConfig.INSTANCE.enableJumpBoost) {
            removeAttributeModifier(entity, GTExoLegsAttributes.JUMP_BOOST_ADDITION.get(),
                    JUMP_BOOST_MODIFIER_UUID);
        }
    }

    private boolean isJumpBoostEnabled(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }
        if (tag.contains(JUMP_BOOST_ENABLED_TAG)) {
            return tag.getBoolean(JUMP_BOOST_ENABLED_TAG);
        }
        return false;
    }

    private boolean toggleJumpBoost(ItemStack stack) {
        boolean isEnabled = isJumpBoostEnabled(stack);
        stack.getOrCreateTag().putBoolean(JUMP_BOOST_ENABLED_TAG, !isEnabled);
        return !isEnabled;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide() && player.isShiftKeyDown() && GTExoLegsConfig.INSTANCE.enableJumpBoost) {
            player.displayClientMessage(Component.translatable(toggleJumpBoost(player.getItemInHand(usedHand)) ?
                    "gtexolegs.tooltip.jump_boost.enabled" : "gtexolegs.tooltip.jump_boost.disabled"), true);
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
                                TooltipFlag isAdvanced) {
        if (GTExoLegsConfig.INSTANCE.enableJumpBoost) {
            tooltip.add(Component.translatable(isJumpBoostEnabled(stack) ? "gtexolegs.tooltip.jump_boost.enabled" :
                    "gtexolegs.tooltip.jump_boost.disabled"));
        }
        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        setAttributeModifier(slotContext.entity(), Attributes.MOVEMENT_SPEED, new AttributeModifier(
                MOVEMENT_SPEED_MULTIPLIER_UUID,
                "Exoskeleton Legs Movement Speed Multiplier",
                GTExoLegsConfig.INSTANCE.movementSpeedMultipliers[tier - 1],
                AttributeModifier.Operation.MULTIPLY_TOTAL));
        if (isJumpBoostEnabled(stack)) {
            setJumpBoost(slotContext.entity());
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return;
        if (player.isShiftKeyDown()) {
            removeAttributeModifier(player, ForgeMod.STEP_HEIGHT_ADDITION.get(), STEP_HEIGHT_MODIFIER_UUID);
        } else {
            setAttributeModifier(player, ForgeMod.STEP_HEIGHT_ADDITION.get(), new AttributeModifier(
                    STEP_HEIGHT_MODIFIER_UUID,
                    "Exoskeleton Legs Step Height Boost",
                    GTExoLegsConfig.INSTANCE.stepHeightModifiers[tier - 1],
                    AttributeModifier.Operation.ADDITION));
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        removeAttributeModifier(slotContext.entity(), ForgeMod.STEP_HEIGHT_ADDITION.get(), STEP_HEIGHT_MODIFIER_UUID);
        removeAttributeModifier(slotContext.entity(), Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_MULTIPLIER_UUID);
        removeJumpBoost(slotContext.entity());
    }

    public static ItemExoskeletonLegs create(Item.Properties properties, int tier) {
        return new ItemExoskeletonLegs(properties.stacksTo(1), tier);
    }
}
