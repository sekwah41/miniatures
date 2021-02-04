package noobanidus.mods.miniatures.client.renderer.entity;

import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import noobanidus.mods.miniatures.client.ModelHolder;
import noobanidus.mods.miniatures.client.model.MiniMeModel;
import noobanidus.mods.miniatures.entity.MiniMeEntity;

import javax.annotation.Nonnull;

public class MiniMeRenderer extends BipedRenderer<MiniMeEntity, MiniMeModel> {

  public MiniMeRenderer(EntityRendererManager renderManager, MiniMeModel model, float shadow) {
    super(renderManager, model, shadow);
  }

  @Override
  public ResourceLocation getEntityTexture(MiniMeEntity entity) {
    return entity.getSkinResourceLocation();
  }

  public static class Factory implements IRenderFactory<MiniMeEntity> {
    @Override
    @Nonnull
    public MiniMeRenderer createRenderFor(@Nonnull EntityRendererManager manager) {
      return new MiniMeRenderer(manager, ModelHolder.miniMe, 0.5f);
    }
  }
}
