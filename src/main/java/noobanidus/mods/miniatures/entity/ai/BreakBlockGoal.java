package noobanidus.mods.miniatures.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import noobanidus.mods.miniatures.MiniTags;
import noobanidus.mods.miniatures.entity.MiniMeEntity;

import java.util.Random;

public class BreakBlockGoal extends Goal {

  private MiniMeEntity entity;
  private PathNavigator pathFinder;
  private BlockPos blockCoord;
  private int tickOffset = 0;
  private Random rand;

  public BreakBlockGoal(MiniMeEntity minime) {
    this.entity = minime;
    this.pathFinder = minime.getNavigator();
    // TODO: Calculate mutexBits
    //setMutexBits(3);
    rand = new Random(minime.getEntityId());
    tickOffset = rand.nextInt(10);
  }

  @Override
  public boolean shouldExecute() {
    if (!pathFinder.noPath()) return false;
    boolean hasTicked = (entity.ticksExisted + tickOffset) % 4 == 0;
    if (hasTicked && entity.world != null && !entity.world.isRemote) {
      for (int i = 0; i < 20; i++) {
        int x = rand.nextInt(16) - 8;
        int y = rand.nextInt(3) - 1;
        int z = rand.nextInt(16) - 8;
        blockCoord = new BlockPos(
            x + entity.getPosX(),
            y + entity.getPosY(),
            z + entity.getPosZ());

        if (canHarvestBlock(blockCoord)) {
          return true;
        }
        blockCoord = null;
      }
    }
    return false;
  }

  @Override
  public void resetTask() {
    pathFinder.clearPath();
    blockCoord = null;
  }

  @Override
  public boolean shouldContinueExecuting() {
    return entity.isAlive() &&
        !pathFinder.noPath() &&
        blockCoord != null &&
        canHarvestBlock(blockCoord);
  }

  @Override
  public void startExecuting() {
    if (blockCoord != null) {
      final Path pathentity = pathFinder.getPathToPos(blockCoord, 6);
      pathFinder.setPath(pathentity, 1.0);
    }
  }

  @Override
  public void tick() {
    super.tick();
    final World world = entity.world;
    if ((world instanceof ServerWorld) && blockCoord != null && canHarvestBlock(blockCoord)) {
      if (entity.getDistanceSq(new Vector3d(blockCoord.getX() + 0.5, blockCoord.getY() + 0.5, blockCoord.getZ() + 0.5)) < 1.0) {
        world.destroyBlock(blockCoord, false, this.entity);
        blockCoord = null;
      }
    }
  }

  public boolean canHarvestBlock(BlockPos coord) {
    return entity.world.getBlockState(coord).isIn(MiniTags.Blocks.BREAK_BLOCKS);
/*    return block instanceof FlowerBlock || block == Blocks.TORCH;*/
  }
}
