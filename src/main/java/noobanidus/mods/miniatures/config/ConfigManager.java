package noobanidus.mods.miniatures.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class ConfigManager {
  private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

  public static final ForgeConfigSpec COMMON_CONFIG;

  private static final ForgeConfigSpec.DoubleValue MAX_HEALTH;
  private static final ForgeConfigSpec.DoubleValue MOVEMENT_SPEED;
  private static final ForgeConfigSpec.DoubleValue ATTACK_DAMAGE;
  private static final ForgeConfigSpec.DoubleValue ARMOR_VALUE;
  private static final ForgeConfigSpec.BooleanValue HOSTILE;
  private static final ForgeConfigSpec.BooleanValue IMMUNE;
  private static final ForgeConfigSpec.BooleanValue DESTROYS_BLOCKS;
  private static final ForgeConfigSpec.BooleanValue BREAKS_BLOCKS;
  private static final ForgeConfigSpec.BooleanValue PICKUP_GOAL;

  static {
    COMMON_BUILDER.comment("options relating to miniatures").push("miniatures");
    MAX_HEALTH = COMMON_BUILDER.comment("the maximum health of a miniature").defineInRange("max_health", 16, 0, Double.MAX_VALUE);
    MOVEMENT_SPEED = COMMON_BUILDER.comment("the base movement speed of a miniature").defineInRange("movement_speed", 0.3, 0, Double.MAX_VALUE);
    ATTACK_DAMAGE = COMMON_BUILDER.comment("if hostile, the amount of damage an attack from a miniature does [1 = 1 full heart, 0.5 = half a heart]").defineInRange("attack_damage", 2.0, 0, Double.MAX_VALUE);
    ARMOR_VALUE = COMMON_BUILDER.comment("how much armor miniatures should have").defineInRange("armor_value", 0, 0, Double.MAX_VALUE);
    HOSTILE = COMMON_BUILDER.comment("whether or not miniatures are hostile to players").define("hostile", false);
    IMMUNE = COMMON_BUILDER.comment("whether or not miniatures are immune to damage that does not originate from a player").define("non_player_immune", true);
    BREAKS_BLOCKS = COMMON_BUILDER.comment("whether or not the miniatures will break blocks in the default tag (miniatures:break_blocks)").define("breaks_blocks", true);
    DESTROYS_BLOCKS = COMMON_BUILDER.comment("whether blocks in the default tag (miniatures:break_blocks) will be destroyed (true) or instead dropped when broken (false)").define("destroys_blocks", false);
    PICKUP_GOAL = COMMON_BUILDER.comment("whether or not non-hostile miniatures will try to pick up players").define("pickup_goal", true);
    COMMON_BUILDER.pop();
    COMMON_CONFIG = COMMON_BUILDER.build();
  }

  public static void loadConfig(ForgeConfigSpec spec, Path path) {
    CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
    configData.load();
    spec.setConfig(configData);
  }

  public static double getArmorValue() {
    return ARMOR_VALUE.get();
  }

  public static double getAttackDamage() {
    return ATTACK_DAMAGE.get();
  }

  public static double getMaxHealth() {
    return MAX_HEALTH.get();
  }

  public static double getMovementSpeed() {
    return MOVEMENT_SPEED.get();
  }

  public static boolean getHostile() {
    return HOSTILE.get();
  }

  public static boolean getImmune() {
    return IMMUNE.get();
  }

  public static boolean getDestroysBlocks () {
    return DESTROYS_BLOCKS.get();
  }

  public static boolean getBreaksBlocks () {
    return BREAKS_BLOCKS.get();
  }

  public static boolean getDoesPickup () {
    return PICKUP_GOAL.get();
  }
}
