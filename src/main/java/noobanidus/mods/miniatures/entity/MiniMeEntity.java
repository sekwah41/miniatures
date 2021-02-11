package noobanidus.mods.miniatures.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import noobanidus.mods.miniatures.config.ConfigManager;
import noobanidus.mods.miniatures.entity.ai.BreakBlockGoal;
import noobanidus.mods.miniatures.entity.ai.PickupPlayerGoal;
import noobanidus.mods.miniatures.init.ModSerializers;
import noobanidus.mods.miniatures.util.SkinUtil;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Optional;

public class MiniMeEntity extends CreatureEntity {
  private static final DataParameter<Optional<GameProfile>> GAMEPROFILE = EntityDataManager.createKey(MiniMeEntity.class, ModSerializers.OPTIONAL_GAME_PROFILE);
  public static final DataParameter<Boolean> SLIM = EntityDataManager.createKey(MiniMeEntity.class, DataSerializers.BOOLEAN);

  private static PlayerProfileCache profileCache;
  private static MinecraftSessionService sessionService;
  private int pickupCooldown = 0;
  private boolean wasRidden = false;

  @Nullable
  public static GameProfile updateGameProfile(@Nullable GameProfile input) {
    if (input != null && !StringUtils.isNullOrEmpty(input.getName())) {
      if (input.isComplete() && input.getProperties().containsKey("textures")) {
        return input;
      } else if (profileCache != null && sessionService != null) {
        GameProfile gameprofile = profileCache.getGameProfileForUsername(input.getName());
        if (gameprofile == null) {
          return input;
        } else {
          Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), (Property) null);
          if (property == null) {
            gameprofile = sessionService.fillProfileProperties(gameprofile, true);
          }

          return gameprofile;
        }
      } else {
        return input;
      }
    } else {
      return input;
    }
  }

  public static void setProfileCache(PlayerProfileCache profileCache) {
    MiniMeEntity.profileCache = profileCache;
  }

  public static void setSessionService(MinecraftSessionService sessionService) {
    MiniMeEntity.sessionService = sessionService;
  }

  public MiniMeEntity(EntityType<? extends MiniMeEntity> type, World world) {
    super(type, world);
    enablePersistence();
  }

  @Override
  protected void registerData() {
    super.registerData();
    this.dataManager.register(GAMEPROFILE, Optional.empty());
    this.dataManager.register(SLIM, false);
  }

  public void setSlim(boolean slim) {
    dataManager.set(SLIM, slim);
  }

  public boolean isSlim() {
    return dataManager.get(SLIM);
  }

  public static AttributeModifierMap.MutableAttribute attributes() {
    return MobEntity.func_233666_p_().createMutableAttribute(Attributes.FOLLOW_RANGE, 35.0D).createMutableAttribute(Attributes.MAX_HEALTH, ConfigManager.getMaxHealth()).createMutableAttribute(Attributes.MOVEMENT_SPEED, ConfigManager.getMovementSpeed()).createMutableAttribute(Attributes.ATTACK_DAMAGE, ConfigManager.getAttackDamage()).createMutableAttribute(Attributes.ARMOR, ConfigManager.getArmorValue());
  }

  @Override
  protected void registerGoals() {
    if (ConfigManager.getHostile()) {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0d, true));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }
    this.goalSelector.addGoal(2, new SwimGoal(this));
    this.goalSelector.addGoal(3, new PickupPlayerGoal(this));
    this.goalSelector.addGoal(4, new BreakBlockGoal(this));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
    this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
    this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
  }

  public MiniMeEntity(EntityType<? extends MiniMeEntity> type, World world, GameProfile owner) {
    this(type, world);
    if (owner != null) {
      dataManager.set(GAMEPROFILE, Optional.of(owner));
    }
  }

  public Optional<GameProfile> getGameProfile() {
    return dataManager.get(GAMEPROFILE);
  }

  public void setGameProfile(GameProfile playerProfile) {
    GameProfile profile = updateGameProfile(playerProfile);
    if (profile != null) {
      dataManager.set(GAMEPROFILE, Optional.of(profile));

      this.setSlim(profile.getId() != null && SkinUtil.isSlimSkin(profile.getId()));
    }
  }

  @Override
  protected PathNavigator createNavigator(World worldIn) {
    GroundPathNavigator navigator = new GroundPathNavigator(this, worldIn);
    setPathPriority(PathNodeType.WATER, -1.0F);
    navigator.setCanSwim(true);
    return navigator;
  }

  @Override
  public void tick() {
    super.tick();
    if (pickupCooldown > 0) pickupCooldown--;
    if (wasRidden && !isBeingRidden()) {
      wasRidden = false;
      setPickupCooldown(1200);
    } else if (isBeingRidden()) {
      wasRidden = true;
    }
  }

  @Override
  public double getMountedYOffset() {
    return this.getSize(this.getPose()).height * 0.15;
  }

  public int getPickupCooldown() {
    return pickupCooldown;
  }

  public void setPickupCooldown(int cooldown) {
    pickupCooldown = cooldown;
  }

  @Override
  public void setCustomName(@Nullable ITextComponent name) {
    super.setCustomName(name);

    if (name != null) {
      this.setGameProfile(new GameProfile(null, name.getUnformattedComponentText().toLowerCase(Locale.ROOT)));
    }
  }

  @Override
  public boolean canDespawn(double distance) {
    return false;
  }

  @Override
  public boolean isChild() {
    return true;
  }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);

    compound.putBoolean("gameProfileExists", dataManager.get(GAMEPROFILE).isPresent());
    if (getGameProfile().isPresent()) {
      compound.put("gameProfile", NBTUtil.writeGameProfile(new CompoundNBT(), dataManager.get(GAMEPROFILE).get()));
    }
    compound.putBoolean("Slim", isSlim());

    compound.putInt("pickupCooldown", pickupCooldown);
  }

  @Override
  public void readAdditional(CompoundNBT tag) {
    super.readAdditional(tag);
    this.pickupCooldown = tag.getInt("pickupCooldown");
    this.setSlim(tag.getBoolean("Slim"));
  }

  @Override
  public void read(CompoundNBT compound) {
    super.read(compound);

    if (compound.contains("owner", Constants.NBT.TAG_STRING)) {
      setGameProfile(new GameProfile(null, compound.getString("owner")));
    } else if (compound.hasUniqueId("OwnerUUID")) {
      setGameProfile(new GameProfile(compound.getUniqueId("OwnerUUID"), null));
    } else {
      dataManager.set(GAMEPROFILE, !compound.getBoolean("gameProfileExists") ? Optional.empty() : Optional.ofNullable(NBTUtil.readGameProfile(compound.getCompound("gameProfile"))));
    }
  }
}
