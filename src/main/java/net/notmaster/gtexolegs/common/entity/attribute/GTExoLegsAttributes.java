package net.notmaster.gtexolegs.common.entity.attribute;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.notmaster.gtexolegs.GTExoLegs;

@Mod.EventBusSubscriber(modid = GTExoLegs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GTExoLegsAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister
            .create(ForgeRegistries.Keys.ATTRIBUTES, GTExoLegs.MOD_ID);
    public static final RegistryObject<Attribute> JUMP_BOOST_ADDITION = ATTRIBUTES.register("jump_height_addition",
            () -> new RangedAttribute("gtexolegs.jump_height", 0.0D, 0.0D, 1024.0D).setSyncable(true));

    public static void init(IEventBus modEventBus) {
        ATTRIBUTES.register(modEventBus);
    }

    @SubscribeEvent
    public static void addAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, JUMP_BOOST_ADDITION.get());
    }
}
