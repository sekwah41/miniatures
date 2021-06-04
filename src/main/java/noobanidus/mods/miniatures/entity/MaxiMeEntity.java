package noobanidus.mods.miniatures.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.world.World;
import noobanidus.mods.miniatures.config.ConfigManager;

public class MaxiMeEntity extends MiniMeEntity {
  public MaxiMeEntity(EntityType<? extends MiniMeEntity> type, World world) {
    super(type, world);
    enablePersistence();
  }

  public static AttributeModifierMap.MutableAttribute attributes() {
    return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, ConfigManager.getMaxHealth()).createMutableAttribute(Attributes.MOVEMENT_SPEED, ConfigManager.getMovementSpeed()).createMutableAttribute(Attributes.ATTACK_DAMAGE, ConfigManager.getAttackDamage()).createMutableAttribute(Attributes.ARMOR, ConfigManager.getArmorValue());
  }

  public MaxiMeEntity(EntityType<? extends MiniMeEntity> type, World world, GameProfile owner) {
    super(type, world, owner);
  }

  @Override
  public double getYOffset() {
    return 0;
  }

  @Override
  public boolean isChild() {
    return false;
  }

  @Override
  protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
    return 6.25f;
  }
}
