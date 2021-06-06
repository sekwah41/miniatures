package noobanidus.mods.miniatures.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class MeEntity extends MiniMeEntity {
  public MeEntity(EntityType<? extends MiniMeEntity> type, World world) {
    super(type, world);
    this.adult = true;
  }

  public MeEntity(EntityType<? extends MiniMeEntity> type, World world, GameProfile owner) {
    super(type, world, owner);
    this.adult = true;
  }
}
