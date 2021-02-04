package noobanidus.mods.miniatures.setup;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import noobanidus.mods.miniatures.init.ModEntities;

public class CommonSetup {
  public static void init(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      ModEntities.registerEntities();
    });
  }
}
