package noobanidus.mods.miniatures.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.common.util.Constants;
import noobanidus.mods.miniatures.MiniTags;
import noobanidus.mods.miniatures.config.ConfigManager;
import noobanidus.mods.miniatures.entity.ai.MiniBreakBlockGoal;
import noobanidus.mods.miniatures.entity.ai.MiniMeleeAttackGoal;
import noobanidus.mods.miniatures.entity.ai.PickupPlayerGoal;
import noobanidus.mods.miniatures.init.ModModifiers;
import noobanidus.mods.miniatures.init.ModSerializers;
import noobanidus.mods.miniatures.util.NoobUtil;
import noobanidus.mods.miniatures.util.SkinUtil;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Optional;

public class MiniMeEntity extends MonsterEntity {
  private static final DataParameter<Optional<GameProfile>> GAMEPROFILE = EntityDataManager.createKey(MiniMeEntity.class, ModSerializers.OPTIONAL_GAME_PROFILE);
  public static final DataParameter<Integer> AGGRO = EntityDataManager.createKey(MiniMeEntity.class, DataSerializers.VARINT);
  public static final DataParameter<Byte> NOOB = EntityDataManager.createKey(MiniMeEntity.class, DataSerializers.BYTE);
  public static final DataParameter<Float> SCALE = EntityDataManager.createKey(MiniMeEntity.class, DataSerializers.FLOAT);

  private ServerBossInfo bossInfo;

  private static PlayerProfileCache profileCache;
  private static MinecraftSessionService sessionService;
  private int pickupCooldown = 0;
  private boolean wasRidden = false;
  protected boolean adult = false;

  private boolean healthBoosted = false;
  private boolean attackBoosted = false;
  private int scaleChanged = -1;
  private boolean isSlim;

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
          Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), null);
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

  public boolean getHostile() {
    int aggro = getAggro();
    if (aggro == -1) {
      return ConfigManager.getHostile();
    }
    return aggro == 1;
  }

  // TODO: Properly handle the relative offset for huge entities? And tiny.
  @Override
  public double getMountedYOffset() {
    return super.getMountedYOffset();
  }

  @Override
  public boolean func_230292_f_(PlayerEntity p_230292_1_) {
    return false;
  }

  @Override
  public boolean canRenderOnFire() {
    if (getNoobVariant() == 2) {
      return true;
    }
    return super.canRenderOnFire();
  }

  @Override
  protected void registerData() {
    super.registerData();
    this.dataManager.register(GAMEPROFILE, Optional.empty());
    this.dataManager.register(AGGRO, -1);
    this.dataManager.register(NOOB, (byte) rand.nextInt(20));
    this.dataManager.register(SCALE, 1f);

    // 0: Upside down
    // 1: Floating
    // 2: On Fire
    // 3: Ghost
    // 4: Glow
    // 5:
  }

  public int getNoobVariant() {
    if (!NoobUtil.isNoob(this)) {
      return -1;
    }
    return (int) (byte) dataManager.get(NOOB);
  }

  public void setNoobVariant(int variant) {
    dataManager.set(NOOB, (byte) variant);
  }

  public float getScale() {
    return dataManager.get(SCALE);
  }

  public void setScale(float scale) {
    dataManager.set(SCALE, scale);
  }

  public void setSlim(boolean slim) {
    this.isSlim = slim;
  }

  public boolean isSlim() {
    return this.isSlim;
  }

  public int getAggro() {
    return dataManager.get(AGGRO);
  }

  public void setAggro(int aggro) {
    dataManager.set(AGGRO, aggro);
  }

  public static AttributeModifierMap.MutableAttribute attributes() {
    return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, ConfigManager.getMaxHealth()).createMutableAttribute(Attributes.MOVEMENT_SPEED, ConfigManager.getMovementSpeed()).createMutableAttribute(Attributes.ATTACK_DAMAGE, ConfigManager.getAttackDamage()).createMutableAttribute(Attributes.ARMOR, ConfigManager.getArmorValue());
  }

  @Override
  protected void registerGoals() {
    this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    this.goalSelector.addGoal(1, new MiniMeleeAttackGoal(this, 1.0d, false));
    this.goalSelector.addGoal(2, new SwimGoal(this));
    this.goalSelector.addGoal(3, new MiniBreakBlockGoal(MiniTags.Blocks.BREAK_BLOCKS, this, 1, 3));
    this.goalSelector.addGoal(4, new PickupPlayerGoal(this));
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
  public boolean attackEntityFrom(DamageSource source, float amount) {
    if (ConfigManager.getImmune() && !(source.getTrueSource() instanceof PlayerEntity) && source != DamageSource.OUT_OF_WORLD) {
      return false;
    }
    return super.attackEntityFrom(source, amount);
  }

  @Override
  public void tick() {
    super.tick();
    if (pickupCooldown > 0) pickupCooldown--;
    if (wasRidden && !isBeingRidden()) {
      wasRidden = false;
    } else if (isBeingRidden()) {
      wasRidden = true;
    }
    if (world.isRemote) {
      int noob = getNoobVariant();
      if (ticksExisted % 4 == 0 && noob == 1) {
        world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, getPosX(), getPosY() + 0.3, getPosZ(), 0, 0, 0);
      }
    }
    if (scaleChanged == -1) {
      scaleChanged = ticksExisted + 20;
    } else if (this.ticksExisted > scaleChanged && scaleChanged != 0) {
      this.recalculateSize();
      scaleChanged = 0;
    }
    if (bossInfo != null) {
      this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }
  }

  @Override
  public double getYOffset() {
    return super.getYOffset();
  }

  @Override
  public Vector3d func_230268_c_(LivingEntity livingEntity) {
    return new Vector3d(this.getPosX(), this.getBoundingBox().minY, this.getPosZ());
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
      if (!dataManager.get(GAMEPROFILE).isPresent()) {
        this.setGameProfile(new GameProfile(null, name.getUnformattedComponentText().toLowerCase(Locale.ROOT)));
      }
      if (bossInfo != null) {
        this.bossInfo.setName(name);
      }
    }
  }

  @Override
  public boolean canDespawn(double distance) {
    return false;
  }

  @Override
  public boolean isChild() {
    return !adult;
  }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);

    compound.putBoolean("gameProfileExists", dataManager.get(GAMEPROFILE).isPresent());
    if (getGameProfile().isPresent()) {
      compound.put("gameProfile", NBTUtil.writeGameProfile(new CompoundNBT(), dataManager.get(GAMEPROFILE).get()));
    }
    compound.putByte("Noob", (byte) getNoobVariant());
    compound.putFloat("Scale", getScale());

    compound.putInt("pickupCooldown", pickupCooldown);
    if (healthBoosted) {
      ModifiableAttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
      if (health != null) {
        AttributeModifier mod = health.getModifier(ModModifiers.HEALTH_INCREASE);
        if (mod != null) {
          compound.putDouble("HealthAddition", mod.getAmount());
          compound.putBoolean("HealthWasBoosted", true);
        }
      }
    }
    if (attackBoosted) {
      ModifiableAttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attack != null) {
        AttributeModifier mod = attack.getModifier(ModModifiers.ATTACK_DAMAGE_INCREASE);
        if (mod != null) {
          compound.putDouble("AttackAddition", mod.getAmount());
        }
      }
    }

    compound.putInt("Hostile", getAggro());

    if (bossInfo != null) {
      compound.putBoolean("BossBar", true);
      compound.putString("BossBarColor", bossInfo.getColor().getName());
      compound.putString("BossBarOverlay", bossInfo.getOverlay().getName());
    }
  }

  @Override
  public void readAdditional(CompoundNBT tag) {
    super.readAdditional(tag);
    this.pickupCooldown = tag.getInt("pickupCooldown");
    if (tag.contains("Noob")) {
      this.setNoobVariant(tag.getByte("Noob"));
    }
    if (tag.contains("Scale")) {
      this.setScale(tag.getFloat("Scale"));
    }
    if (tag.contains("Hostile")) {
      this.setAggro(tag.getInt("Hostile"));
    }
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

    if (compound.contains("NameTag", Constants.NBT.TAG_STRING)) {
      dataManager.set(CUSTOM_NAME, Optional.of(new StringTextComponent(compound.getString("NameTag"))));
    }
    if (compound.contains("AttackAddition")) {
      ModifiableAttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attack != null) {
        double value = 0.0;
        if (compound.contains("AttackAddition", Constants.NBT.TAG_FLOAT)) {
          value = (double) compound.getFloat("AttackAddition");
        } else if (compound.contains("AttackAddition", Constants.NBT.TAG_INT)) {
          value = (double) compound.getInt("AttackAddition");
        } else if (compound.contains("AttackAddition", Constants.NBT.TAG_DOUBLE)) {
          value = compound.getDouble("AttackAddition");
        }
        if (value != 0.0) {
          if (attack.getModifier(ModModifiers.ATTACK_DAMAGE_INCREASE) != null) {
            attack.removeModifier(ModModifiers.ATTACK_DAMAGE_INCREASE);
          }
          attack.applyPersistentModifier(new AttributeModifier(ModModifiers.ATTACK_DAMAGE_INCREASE, "Sublimiter Torture Mechanism", value, AttributeModifier.Operation.ADDITION));
          attackBoosted = true;
        }
      }
    }
    if (compound.contains("HealthAddition")) {
      ModifiableAttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
      if (health != null) {
        double value = 0.0;
        if (compound.contains("HealthAddition", Constants.NBT.TAG_FLOAT)) {
          value = (double) compound.getFloat("HealthAddition");
        } else if (compound.contains("HealthAddition", Constants.NBT.TAG_INT)) {
          value = (double) compound.getInt("HealthAddition");
        } else if (compound.contains("HealthAddition", Constants.NBT.TAG_DOUBLE)) {
          value = compound.getDouble("HealthAddition");
        }
        if (value != 0.0) {
          if (health.getModifier(ModModifiers.HEALTH_INCREASE) != null) {
            health.removeModifier(ModModifiers.HEALTH_INCREASE);
          }
          health.applyPersistentModifier(new AttributeModifier(ModModifiers.HEALTH_INCREASE, "Sublimiter Torture Mechanism", value, AttributeModifier.Operation.ADDITION));
          if (!compound.contains("HealthWasBoosted") || !compound.getBoolean("HealthWasBoosted")) {
            this.heal((float) value);
          }
          healthBoosted = true;
        }
      }
    }
    if (compound.contains("BossBar", Constants.NBT.TAG_BYTE) && compound.getBoolean("BossBar")) {
      BossInfo.Color bossInfoColor = BossInfo.Color.WHITE;
      BossInfo.Overlay bossInfoOverlay = BossInfo.Overlay.PROGRESS;
      if (compound.contains("BossBarColor", Constants.NBT.TAG_STRING)) {
        bossInfoColor = BossInfo.Color.byName(compound.getString("BossBarColor").toLowerCase());
      }
      if (compound.contains("BossBarOverlay", Constants.NBT.TAG_STRING)) {
        bossInfoOverlay = BossInfo.Overlay.byName(compound.getString("BossBarOverlay").toLowerCase());
      }
      bossInfo = new ServerBossInfo(TextComponentUtils.getDisplayName(getGameProfile().get()), bossInfoColor, bossInfoOverlay);
    }
  }

  @Override
  public void addTrackingPlayer(ServerPlayerEntity player) {
    super.addTrackingPlayer(player);
    if (bossInfo != null) {
      this.bossInfo.addPlayer(player);
    }
  }

  @Override
  public void removeTrackingPlayer(ServerPlayerEntity player) {
    super.removeTrackingPlayer(player);
    if (bossInfo != null) {
      this.bossInfo.removePlayer(player);
    }
  }

  @Override
  public void notifyDataManagerChange(DataParameter<?> key) {
    if(GAMEPROFILE.equals(key) && this.world.isRemote()) {
      GameProfile gameprofile = this.getGameProfile().get();
      if(gameprofile != null) {
        Minecraft.getInstance().getSkinManager().loadProfileTextures(gameprofile, (textureType, textureLocation, profileTexture) -> {
          if (textureType.equals(MinecraftProfileTexture.Type.SKIN))  {
            String metadata = profileTexture.getMetadata("model");
            this.setSlim(metadata != null && metadata.equals("slim"));
          }
        }, true);
      }
    }
  }

  @Override
  protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
    return p_213348_2_.height * 0.93f;
  }

  @Override
  public float getRenderScale() {
    return 1.0f;
  }
}
