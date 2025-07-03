package collisiondamage;

import collisiondamage.handler.CollisionEventHandler;
import collisiondamage.handler.PotionRegistryHandler;
import collisiondamage.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = CollisionDamageMod.MOD_ID, name = "Collision Damage Mod", version = "1.0")
public class CollisionDamageMod {
    public static final String MOD_ID = "collisiondamage";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(new CollisionEventHandler());
        MinecraftForge.EVENT_BUS.register(PotionRegistryHandler.class);

        // 注册net
        PacketHandler.registerMessages();
    }
}
