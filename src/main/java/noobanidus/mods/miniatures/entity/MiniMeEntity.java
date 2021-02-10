package noobanidus.mods.miniatures.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.PacketDistributor;
import noobanidus.mods.miniatures.config.ConfigManager;
import noobanidus.mods.miniatures.entity.ai.BreakBlockGoal;
import noobanidus.mods.miniatures.entity.ai.PickupPlayerGoal;
import noobanidus.mods.miniatures.network.Networking;
import noobanidus.mods.miniatures.network.OwnerChanged;
import noobanidus.mods.miniatures.util.GameProfileSerializer;
import org.jline.utils.Log;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class MiniMeEntity extends CreatureEntity implements IEntityAdditionalSpawnData {
  @OnlyIn(Dist.CLIENT)
  private ResourceLocation locationSkin;
  private GameProfile owner;
  private int pickupCooldown = 0;
  private boolean wasRidden = false;

  public MiniMeEntity(EntityType<? extends MiniMeEntity> type, World world) {
    super(type, world);
    // setSize(0.6F, 0.95F); <-- entity type
    enablePersistence();
  }

  public static AttributeModifierMap.MutableAttribute attributes() {
    return MobEntity.func_233666_p_().createMutableAttribute(Attributes.FOLLOW_RANGE, 35.0D).createMutableAttribute(Attributes.MAX_HEALTH, ConfigManager.getMaxHealth()).createMutableAttribute(Attributes.MOVEMENT_SPEED, ConfigManager.getMovementSpeed()).createMutableAttribute(Attributes.ATTACK_DAMAGE, ConfigManager.getAttackDamage()).createMutableAttribute(Attributes.ARMOR, ConfigManager.getArmorValue());
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new PickupPlayerGoal(this));
    this.goalSelector.addGoal(3, new BreakBlockGoal(this));
    // PickupPlayer
    // BreakBlock

    this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
    this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 8.0F));

    if (ConfigManager.getHostile()) {
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0d, true));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    // Attack goal this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
    // this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
  }

  public MiniMeEntity(EntityType<? extends MiniMeEntity> type, World world, GameProfile owner) {
    this(type, world);
    this.owner = owner != null ? SkullTileEntity.updateGameProfile(owner) : null;
  }

  public void setOwner(GameProfile owner) {
    this.owner = owner;
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

    if (!world.isRemote) {
      if (name != null && (owner == null || !name.getString().equalsIgnoreCase(owner.getName()))) {
        try {
          this.owner = SkullTileEntity.updateGameProfile(new GameProfile(null, name.getString()));
          propagateOwnerChange();
        } catch (Exception e) {
          Log.warn(e, "Failed to change skin to %s", name);
        }
      }
    }
  }

  private void propagateOwnerChange() {
    Networking.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new OwnerChanged(getEntityId(), owner));
  }

  @OnlyIn(Dist.CLIENT)
  @Nullable
  public ResourceLocation getSkinResourceLocation() {
    if (owner != null) {
      final SkinManager manager = Minecraft.getInstance().getSkinManager();
      Map<Type, MinecraftProfileTexture> map = manager.loadSkinFromCache(owner);

      if (map.containsKey(Type.SKIN)) {
        final MinecraftProfileTexture skin = map.get(Type.SKIN);
        return manager.loadSkin(skin, Type.SKIN);
      } else {
        UUID uuid = PlayerEntity.getUUID(owner);
        return DefaultPlayerSkin.getDefaultSkin(uuid);
      }
    }

    return null;
  }

  @Override
  public boolean canDespawn(double distance) {
    return false;
  }

  @Override
  public boolean isChild() {
    return true;
  }

  public GameProfile getOwner() {
    return owner;
  }

  @Override
  public void writeSpawnData(PacketBuffer data) {
    if (owner != null) {
      data.writeBoolean(true);
      GameProfileSerializer.write(owner, data);
    } else data.writeBoolean(false);
  }

  @Override
  public void readSpawnData(PacketBuffer data) {
    if (data.readBoolean()) {
      this.owner = GameProfileSerializer.read(data);
    }
  }

  @Override
  public void writeAdditional(CompoundNBT tag) {
    super.writeAdditional(tag);

    if (owner != null) {
      CompoundNBT ownerTag = new CompoundNBT();
      NBTUtil.writeGameProfile(ownerTag, owner);
      tag.put("Owner", ownerTag);
    }

    tag.putInt("pickupCooldown", pickupCooldown);
  }

  @Override
  public void readAdditional(CompoundNBT tag) {
    this.owner = readOwner(tag);

    if (owner != null) {
      propagateOwnerChange();
    }

    // switched order, to prevent needless profile fetch in setCustomName
    super.readAdditional(tag);

    this.pickupCooldown = tag.getInt("pickupCooldown");
  }

  private static GameProfile readOwner(CompoundNBT tag) {
    if (tag.contains("owner", Constants.NBT.TAG_STRING)) {
      String ownerName = tag.getString("owner");
      return SkullTileEntity.updateGameProfile(new GameProfile(null, ownerName));
    } else if (tag.hasUniqueId("OwnerUUID")) {
      UUID uuid = tag.getUniqueId("OwnedUUID");
      return new GameProfile(uuid, null);
    } else if (tag.contains("Owner", Constants.NBT.TAG_COMPOUND)) {
      return NBTUtil.readGameProfile(tag.getCompound("Owner"));
    }

    return null;
  }

}
