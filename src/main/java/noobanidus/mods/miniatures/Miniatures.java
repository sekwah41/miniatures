package noobanidus.mods.miniatures;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import noobanidus.libs.noobutil.registrate.CustomRegistrate;
import noobanidus.mods.miniatures.config.ConfigManager;
import noobanidus.mods.miniatures.entity.MiniMeEntity;
import noobanidus.mods.miniatures.init.ModEntities;
import noobanidus.mods.miniatures.init.ModSerializers;
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
    MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);

    REGISTRATE = CustomRegistrate.create(MODID);
    ModEntities.load();
    ModTags.load();
    ModSerializers.load();
  }

  public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
    MinecraftServer server = event.getServer();
    MiniMeEntity.setProfileCache(server.getPlayerProfileCache());
    MiniMeEntity.setSessionService(server.getMinecraftSessionService());
    PlayerProfileCache.setOnlineMode(server.isServerInOnlineMode());
  }
}
