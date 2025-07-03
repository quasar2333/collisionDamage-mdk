package collisiondamage.effect;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import collisiondamage.CollisionDamageMod;

public class CollisionDamageEffect extends Potion {
    public static final CollisionDamageEffect INSTANCE = new CollisionDamageEffect();

    private CollisionDamageEffect() {
        super(false, 0xFFFFFF); // 设置药水颜色
        this.setRegistryName(new ResourceLocation(CollisionDamageMod.MOD_ID, "collision_damage"));
        this.setPotionName("collision_damage"); // 使用语言文件中的药水名称
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true; // 每tick触发
    }
}
