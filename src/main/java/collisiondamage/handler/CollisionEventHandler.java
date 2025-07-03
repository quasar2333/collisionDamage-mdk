package collisiondamage.handler;

import collisiondamage.effect.CollisionDamageEffect;
import collisiondamage.network.PacketHandler;
import collisiondamage.network.SpeedSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CollisionEventHandler {
    private static final double RADIUS = 1.0;
    private static final String SPEED_KEY = "collisiondamage.speed";

    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();

        // 实体有碰撞伤害效果时
        if (entity.isPotionActive(CollisionDamageEffect.INSTANCE)) {
            double speed = entity.getEntityData().getDouble(SPEED_KEY); // 读nbt

            if (speed > 10.0) {
                for (Entity target : entity.world.getEntitiesWithinAABBExcludingEntity(entity, entity.getEntityBoundingBox().grow(RADIUS))) {
                    if (target instanceof EntityLivingBase) {
                        int level = entity.getActivePotionEffect(CollisionDamageEffect.INSTANCE).getAmplifier() + 1;
                        double damage = 0.1 * speed * level; // 同步速度计算伤害
                        target.attackEntityFrom(DamageSource.causeMobDamage(entity), (float) damage);

                        // 设置水平击退效果
                        double knockbackStrength = 0.5;
                        double dx = target.posX - entity.posX;
                        double dz = target.posZ - entity.posZ;
                        double distance = MathHelper.sqrt(dx * dx + dz * dz);
                        if (distance != 0) {
                            target.motionX += (dx / distance) * knockbackStrength;
                            target.motionZ += (dz / distance) * knockbackStrength;
                        }
                    }
                }
            }
        }
    }

    private double calculateSpeed(EntityLivingBase entity) {
        double dx = entity.posX - entity.prevPosX;
        double dy = entity.posY - entity.prevPosY;
        double dz = entity.posZ - entity.prevPosZ;
        double distancePerTick = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        double ticksPerSecond = 20.0;
        return distancePerTick * ticksPerSecond;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.player != null && mc.player.isPotionActive(CollisionDamageEffect.INSTANCE)) {
                double speed = calculateSpeed(mc.player);

                // 显示速度
                ScaledResolution resolution = new ScaledResolution(mc);
                int x = resolution.getScaledWidth() / 2 + 10;
                int y = resolution.getScaledHeight() - 49;

                mc.fontRenderer.drawString(
                        "Speed: " + String.format("%.2f", speed) + " m/s",
                        x,
                        y,
                        0xFFFFFF
                );

                // 发送到服务器
                mc.player.getEntityData().setDouble(SPEED_KEY, speed); // 在客户端
                PacketHandler.INSTANCE.sendToServer(new SpeedSyncPacket(speed)); // 2024.11.8
            }
        }
    }
}
