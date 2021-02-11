package noobanidus.mods.miniatures.entity.ai;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.world.World;
import noobanidus.mods.miniatures.entity.MiniMeEntity;

import java.util.EnumSet;
import java.util.List;

public class PickupPlayerGoal extends Goal {

  private MiniMeEntity minime;
  private PathNavigator pathFinder;
  private PlayerEntity targetPlayer;

  public PickupPlayerGoal(MiniMeEntity entity) {
    this.minime = entity;
    this.pathFinder = entity.getNavigator();
    this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  @Override
  public boolean shouldExecute() {
    if (minime.getPickupCooldown() > 0) return false;
    if (!pathFinder.noPath()) return false;

    if (!minime.world.isRemote && targetPlayer == null || targetPlayer.isAlive()) {
      List<PlayerEntity> players = minime.world.getEntitiesWithinAABB(PlayerEntity.class, minime.getBoundingBox().grow(10));
      for (PlayerEntity player : players) {
        if (canRidePlayer(player)) {
          targetPlayer = player;
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void resetTask() {
    pathFinder.clearPath();
    targetPlayer = null;
  }

  @Override
  public boolean shouldContinueExecuting() {
    return minime.isAlive() &&
        !pathFinder.noPath() &&
        canRidePlayer(targetPlayer);
  }

  @Override
  public void startExecuting() {
    if (targetPlayer != null) {
      pathFinder.tryMoveToXYZ(targetPlayer.getPosX(), targetPlayer.getPosY(), targetPlayer.getPosZ(), 1f);
    }
  }

  @Override
  public void tick() {
    super.tick();
    World world = minime.world;
    if (!world.isRemote && canRidePlayer(targetPlayer)) {
      if (minime.getDistance(targetPlayer) < 1.0) {
        targetPlayer.startRiding(minime);
      }
    }
  }

  private boolean canRidePlayer(PlayerEntity player) {
    final GameProfile owner = minime.getGameProfile().orElse(null);
    return owner != null && player != null && player.getGameProfile().getId().equals(owner.getId()) && player.isPassenger();
  }
}
