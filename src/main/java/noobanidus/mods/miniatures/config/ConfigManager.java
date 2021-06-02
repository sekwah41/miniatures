package noobanidus.mods.miniatures.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

public class ConfigManager {
  private static ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

  public static ForgeConfigSpec COMMON_CONFIG;

  private static ForgeConfigSpec.DoubleValue MAX_HEALTH;
  private static ForgeConfigSpec.DoubleValue MOVEMENT_SPEED;
  private static ForgeConfigSpec.DoubleValue ATTACK_DAMAGE;
  private static ForgeConfigSpec.DoubleValue ARMOR_VALUE;
  private static ForgeConfigSpec.BooleanValue HOSTILE;
  private static ForgeConfigSpec.BooleanValue IMMUNE;

  private static double maxHealth = -999;
  private static double movementSpeed = -999;
  private static double attackDamage = -999;
  private static double armorValue = -999;
  private static int hostile = -999;
  private static int immune = -999;


  static {
    COMMON_BUILDER.comment("options relating to miniatures").push("miniatures");
    MAX_HEALTH = COMMON_BUILDER.comment("the maximum health of a miniature").defineInRange("max_health", 16, 0, Double.MAX_VALUE);
    MOVEMENT_SPEED = COMMON_BUILDER.comment("the base movement speed of a miniature").defineInRange("movement_speed", 0.3, 0, Double.MAX_VALUE);
    ATTACK_DAMAGE = COMMON_BUILDER.comment("if hostile, the amount of damage an attack from a miniature does [1 = 1 full heart, 0.5 = half a heart]").defineInRange("attack_damage", 2.0, 0, Double.MAX_VALUE);
    ARMOR_VALUE = COMMON_BUILDER.comment("how much armor miniatures should have").defineInRange("armor_value", 0, 0, Double.MAX_VALUE);
    HOSTILE = COMMON_BUILDER.comment("whether or not miniatures are hostile to players").define("hostile", false);
    IMMUNE = COMMON_BUILDER.comment("whether or not miniatures are immune to damage that does not originate from a player").define("non_player_immune", true);
    COMMON_BUILDER.pop();
    COMMON_CONFIG = COMMON_BUILDER.build();
  }

  public static void loadConfig(ForgeConfigSpec spec, Path path) {
    CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
    configData.load();
    spec.setConfig(configData);
  }

  public static void configLoaded(ModConfig.ModConfigEvent event) {
    hostile = -999;
    movementSpeed = -999;
    maxHealth = -999;
    attackDamage = -999;
    armorValue = -999;
    immune = -999;
  }

  public static double getArmorValue() {
    if (armorValue == -999) {
      armorValue = ARMOR_VALUE.get();
    }
    return armorValue;
  }

  public static double getAttackDamage() {
    if (attackDamage == -999) {
      attackDamage = ATTACK_DAMAGE.get();
    }
    return attackDamage;
  }

  public static double getMaxHealth() {
    if (maxHealth == -999) {
      maxHealth = MAX_HEALTH.get();
    }
    return maxHealth;
  }

  public static double getMovementSpeed() {
    if (movementSpeed == -999) {
      movementSpeed = MOVEMENT_SPEED.get();
    }
    return movementSpeed;

  }

  public static boolean getHostile() {
    if (hostile == -999) {
      hostile = HOSTILE.get() ? 1 : 0;
    }
    return hostile == 1;
  }

  public static boolean getImmune () {
    if (immune == -999) {
      immune = IMMUNE.get() ? 1 : 0;
    }
    return immune == 1;
  }
}
