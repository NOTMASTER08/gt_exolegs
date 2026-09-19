package net.notmaster.gtexolegs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.notmaster.gtexolegs.GTExoLegs;
import net.notmaster.gtexolegs.common.item.ItemExoskeletonLegs;
import net.notmaster.gtexolegs.network.PacketHandler;
import net.notmaster.gtexolegs.network.serverbound.PacketScroll;

@Mod.EventBusSubscriber(modid = GTExoLegs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void scrollEvent(InputEvent.MouseScrollingEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (player.isShiftKeyDown() &&
                player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemExoskeletonLegs) {
            PacketHandler.channel
                    .sendToServer(new PacketScroll(event.getScrollDelta() > 0 ? 1 : -1, EquipmentSlot.MAINHAND));
            event.setCanceled(true);
        }
    }
}
