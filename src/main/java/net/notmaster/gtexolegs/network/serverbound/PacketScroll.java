package net.notmaster.gtexolegs.network.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.notmaster.gtexolegs.common.item.ItemExoskeletonLegs;

import java.util.function.Supplier;

public class PacketScroll {

    private final int direction;
    private final EquipmentSlot slot;

    public PacketScroll(int direction, EquipmentSlot slot) {
        this.direction = direction;
        this.slot = slot;
    }

    public void handle(NetworkEvent.Context context) {
        Player player = context.getSender();
        if (player == null) return;
        ItemStack stack = player.getItemBySlot(slot);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemExoskeletonLegs) {
            ItemExoskeletonLegs.scrollSelection(stack, direction, player);
        }
    }

    public static void handle(PacketScroll message, Supplier<NetworkEvent.Context> ctx) {
        if (message == null) return;
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> message.handle(context));
        context.setPacketHandled(true);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(direction);
        buffer.writeEnum(slot);
    }

    public static PacketScroll decode(FriendlyByteBuf buffer) {
        return new PacketScroll(buffer.readVarInt(), buffer.readEnum(EquipmentSlot.class));
    }
}
