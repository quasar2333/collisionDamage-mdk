package collisiondamage.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityLivingBase;

public class SpeedSyncPacket implements IMessage {
    private double speed;

    public SpeedSyncPacket() {}

    public SpeedSyncPacket(double speed) {
        this.speed = speed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.speed = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.speed);
    }

    public static class Handler implements IMessageHandler<SpeedSyncPacket, IMessage> {
        @Override
        public IMessage onMessage(SpeedSyncPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                if (player instanceof EntityLivingBase) {
                    ((EntityLivingBase) player).getEntityData().setDouble("collisiondamage.speed", message.speed);
                }
            });
            return null;
        }
    }
}
