package noobanidus.mods.miniatures.entity.ai;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.world.World;
import noobanidus.mods.miniatures.config.ConfigManager;
import noobanidus.mods.miniatures.entity.MiniMeEntity;

import java.util.EnumSet;
import java.util.List;

public class PickupPlayerGoal extends Goal {

  private MiniMeEntity minime;
  private Path path;
  private PlayerEntity targetPlayer;

  public PickupPlayerGoal(MiniMeEntity entity) {
    this.minime = entity;
    this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  @Override
  public boolean shouldExecute() {
    if (!ConfigManager.getDoesPickup() || this.minime.getHostile()) {
      return false;
    }
    if (minime.getPickupCooldown() > 0) {
      return false;
    }
    if (path != null) {
      return false;
    }
    if (targetPlayer == null || targetPlayer.isAlive()) {
      List<PlayerEntity> players = minime.world.getEntitiesWithinAABB(PlayerEntity.class, minime.getBoundingBox().grow(10));
      for (PlayerEntity player : players) {
        if (player.isPassenger()) {
          continue;
        }
        if (!ConfigManager.getOwnerRider() || canRidePlayer(player)) {
          targetPlayer = player;
          path = minime.getNavigator().getPathToEntity(targetPlayer, 0);
          return path != null;
        }
      }
    }
    return false;
  }

  @Override
  public void resetTask() {
    path = null;
    targetPlayer = null;
    minime.getNavigator().clearPath();
  }

  @Override
  public boolean shouldContinueExecuting() {
    return targetPlayer != null && !targetPlayer.isPassenger() && ConfigManager.getDoesPickup() && !minime.getHostile() && minime.isAlive() && path != null && (!ConfigManager.getOwnerRider() || canRidePlayer(targetPlayer));
  }

  @Override
  public void startExecuting() {
    minime.getNavigator().setPath(this.path, 1);
  }

  @Override
  public void tick() {
    super.tick();
    if (!minime.world.isRemote && (!ConfigManager.getOwnerRider() || canRidePlayer(targetPlayer))) {
      if (minime.getDistance(targetPlayer) < 1.5) {
        targetPlayer.startRiding(minime);
      }
    }
  }

  private boolean canRidePlayer(PlayerEntity player) {
    if (!ConfigManager.getOwnerRider()) {
      return true;
    }
    final GameProfile owner = minime.getGameProfile().orElse(null);
    return owner != null && player != null && player.getGameProfile().getId().equals(owner.getId());
  }
}
