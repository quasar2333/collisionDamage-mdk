package collisiondamage.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import collisiondamage.CollisionDamageMod;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(CollisionDamageMod.MOD_ID);
    private static int id = 0;

    public static void registerMessages() {
        INSTANCE.registerMessage(SpeedSyncPacket.Handler.class, SpeedSyncPacket.class, id++, Side.SERVER);
    }
}
