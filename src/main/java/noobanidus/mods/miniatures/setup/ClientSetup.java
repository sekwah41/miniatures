package noobanidus.mods.miniatures.setup;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import noobanidus.mods.miniatures.client.ModelHolder;
import noobanidus.mods.miniatures.client.renderer.entity.MaxiMeRenderer;
import noobanidus.mods.miniatures.client.renderer.entity.MiniMeRenderer;
import noobanidus.mods.miniatures.init.ModEntities;

@OnlyIn(Dist.CLIENT)
public class ClientSetup {
  public static void init(FMLClientSetupEvent event) {
    ModelHolder.init();
    RenderingRegistry.registerEntityRenderingHandler(ModEntities.MINIME.get(), new MiniMeRenderer.Factory());
    RenderingRegistry.registerEntityRenderingHandler(ModEntities.MAXIME.get(), new MaxiMeRenderer.Factory());
    event.enqueueWork(() -> {
    });
  }
}
