package noobanidus.mods.miniatures.setup;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import noobanidus.mods.miniatures.init.ModEntities;
import noobanidus.mods.miniatures.init.ModSerializers;

public class CommonSetup {
  public static void init(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      DataSerializers.registerSerializer(ModSerializers.OPTIONAL_GAME_PROFILE);
      ModEntities.registerEntities();
    });
  }
}
