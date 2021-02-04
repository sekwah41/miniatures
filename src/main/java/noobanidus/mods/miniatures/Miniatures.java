package noobanidus.mods.miniatures;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import noobanidus.libs.noobutil.registrate.CustomRegistrate;
import noobanidus.mods.miniatures.config.ConfigManager;
import noobanidus.mods.miniatures.init.ModEntities;
import noobanidus.mods.miniatures.init.ModTags;
import noobanidus.mods.miniatures.setup.ClientInit;
import noobanidus.mods.miniatures.setup.CommonSetup;

@Mod("miniatures")
public class Miniatures {
  public static final String MODID = "miniatures";

  public static CustomRegistrate REGISTRATE;

  public Miniatures() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG);
    ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Miniatures.MODID + "-common.toml"));
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    modBus.addListener(CommonSetup::init);

    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientInit::init);

    modBus.addListener(ConfigManager::configLoaded);

    REGISTRATE = CustomRegistrate.create(MODID);
    ModEntities.load();
    ModTags.load();
  }
}
