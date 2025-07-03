package collisiondamage.handler;

import collisiondamage.effect.CollisionDamageEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.potion.Potion;

public class PotionRegistryHandler {
    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(CollisionDamageEffect.INSTANCE);
    }
}
