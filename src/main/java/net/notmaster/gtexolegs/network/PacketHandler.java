package net.notmaster.gtexolegs.network;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.notmaster.gtexolegs.GTExoLegs;
import net.notmaster.gtexolegs.network.serverbound.PacketScroll;

import java.util.Optional;

public class PacketHandler {

    private static final String VERSION = "%d.%d.%d".formatted(
            GTExoLegs.VERSION.getMajorVersion(),
            GTExoLegs.VERSION.getMajorVersion(),
            GTExoLegs.VERSION.getIncrementalVersion());
    public static final SimpleChannel channel = NetworkRegistry.ChannelBuilder
            .named(GTExoLegs.id(GTExoLegs.MOD_ID))
            .clientAcceptedVersions(VERSION::equals)
            .serverAcceptedVersions(VERSION::equals)
            .networkProtocolVersion(() -> VERSION)
            .simpleChannel();

    public static void init() {
        int index = 0;
        channel.registerMessage(index++, PacketScroll.class, PacketScroll::encode, PacketScroll::decode,
                PacketScroll::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
