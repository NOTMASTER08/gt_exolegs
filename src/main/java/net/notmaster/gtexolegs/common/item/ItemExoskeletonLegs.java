package net.notmaster.gtexolegs.common.item;

import net.minecraft.ChatFormatting;
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
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemExoskeletonLegs extends Item implements ICurioItem {

    public enum Features {

        JUMP_BOOST("jump_boost"),
        FLIGHT_MODIFIER("flight_modifier");

        private final String key;
        private static final Map<String, Features> BY_KEY = Arrays.stream(values())
                .collect(Collectors.toUnmodifiableMap((x) -> x.key, Function.identity()));

        Features(String key) {
            this.key = key;
        }

        public static Features fromKey(String key) {
            return BY_KEY.getOrDefault(key, Features.JUMP_BOOST);
        }
    }

    private static final UUID STEP_HEIGHT_MODIFIER_UUID = UUID.fromString("b8a6abba-b241-4795-9ff9-00d98b52ec04");
    private static final UUID MOVEMENT_SPEED_MULTIPLIER_UUID = UUID.fromString("0687d81a-0ef8-4c45-9a5a-a1e6a13719a9");
    private static final UUID JUMP_BOOST_MODIFIER_UUID = UUID.fromString("dfc1fb10-e6a6-4598-902c-8f22b0195b38");
    private static final String SELECTED_FEATURE_TAG = "selectedFeature";

    public final int tier;

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
        setAttributeModifier(entity, GTExoLegsAttributes.JUMP_BOOST_ADDITION.get(),
                new AttributeModifier(
                        JUMP_BOOST_MODIFIER_UUID,
                        "Exoskeleton Legs Jump Bost Modifier",
                        GTExoLegsConfig.INSTANCE.jumpBoostModifiers[tier - 1],
                        AttributeModifier.Operation.ADDITION));
    }

    private void removeJumpBoost(LivingEntity entity) {
        removeAttributeModifier(entity, GTExoLegsAttributes.JUMP_BOOST_ADDITION.get(),
                JUMP_BOOST_MODIFIER_UUID);
    }

    public static boolean isFeatureEnabled(ItemStack stack, Features feature) {
        return stack.hasTag() && stack.getOrCreateTag().getBoolean(feature.key);
    }

    private boolean toggleFeature(ItemStack stack, Features feature) {
        boolean isEnabled = isFeatureEnabled(stack, feature);
        stack.getOrCreateTag().putBoolean(feature.key, !isEnabled);
        return !isEnabled;
    }

    private static Features getSelectedFeature(ItemStack stack) {
        if (!stack.getOrCreateTag().contains(SELECTED_FEATURE_TAG)) return Features.JUMP_BOOST;
        return Features.fromKey(stack.getOrCreateTag().getString(SELECTED_FEATURE_TAG));
    }

    private static void displaySelectedFeature(ItemStack stack, Player player) {
        player.displayClientMessage(createFeatureString(stack, getSelectedFeature(stack)), true);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide() && player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(usedHand);
            Features selected = getSelectedFeature(stack);
            toggleFeature(stack, selected);
            displaySelectedFeature(stack, player);
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }
        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }

    public static void scrollSelection(ItemStack stack, int direction, Player player) {
        Features selected = getSelectedFeature(stack);
        int index = selected.ordinal();
        Features[] values = Features.values();
        selected = values[Math.floorMod(index + direction, values.length)];
        stack.getOrCreateTag().putString(SELECTED_FEATURE_TAG, selected.key);
        displaySelectedFeature(stack, player);
    }

    private static Component createFeatureString(ItemStack stack, Features feature) {
        boolean enabled = isFeatureEnabled(stack, feature);
        return Component.translatable("gtexolegs.tooltip.%s".formatted(feature.key),
                Component.translatable("gtexolegs.tooltip.%s".formatted(enabled ? "enabled" : "disabled"))
                        .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
                                TooltipFlag isAdvanced) {
        tooltip.add(Component.translatable("gtexolegs.tooltip.help1").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("gtexolegs.tooltip.help2").withStyle(ChatFormatting.BLUE));
        for (Features feature : Features.values()) {
            tooltip.add(createFeatureString(stack, feature));
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
        if (isFeatureEnabled(stack, Features.JUMP_BOOST)) {
            setJumpBoost(slotContext.entity());
        }
        if (isFeatureEnabled(stack, Features.FLIGHT_MODIFIER)) {
            if (slotContext.entity() instanceof Player player) {
                player.getAbilities()
                        .setFlyingSpeed(0.05F * GTExoLegsConfig.INSTANCE.creativeFlightSpeedMultipliers[tier - 1]);
                player.onUpdateAbilities();
            }
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
        if (slotContext.entity() instanceof Player player) {
            player.getAbilities().setFlyingSpeed(0.05F);
            player.onUpdateAbilities();
        }
    }

    public static ItemExoskeletonLegs create(Item.Properties properties, int tier) {
        return new ItemExoskeletonLegs(properties.stacksTo(1), tier);
    }

    public static OptionalInt findEquippedTier(Player player) {
        return CuriosApi.getCuriosInventory(player).map(curios -> {
            for (var result : curios.findCurios("exoskeleton_legs")) {
                if (result.stack().getItem() instanceof ItemExoskeletonLegs legs) {
                    if (ItemExoskeletonLegs.isFeatureEnabled(result.stack(),
                            ItemExoskeletonLegs.Features.FLIGHT_MODIFIER)) {
                        return OptionalInt.of(legs.tier);
                    }
                }
            }
            return OptionalInt.empty();
        }).orElse(OptionalInt.empty());
    }
}
