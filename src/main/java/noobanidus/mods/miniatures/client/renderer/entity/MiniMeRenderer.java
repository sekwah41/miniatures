package noobanidus.mods.miniatures.client.renderer.entity;

import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import noobanidus.mods.miniatures.client.ModelHolder;
import noobanidus.mods.miniatures.client.model.MiniMeModel;
import noobanidus.mods.miniatures.entity.MiniMeEntity;

import javax.annotation.Nonnull;

public class MiniMeRenderer extends BipedRenderer<MiniMeEntity, MiniMeModel> {
  private static final ResourceLocation TEXTURE_STEVE = new ResourceLocation("textures/entity/steve.png");

  public MiniMeRenderer(EntityRendererManager renderManager, MiniMeModel model, float shadow) {
    super(renderManager, model, shadow);
    this.addLayer(new BipedArmorLayer<>(this, ModelHolder.miniMe, ModelHolder.miniMe));
    this.addLayer(new HeldItemLayer<>(this));
    this.addLayer(new ArrowLayer<>(this));
    this.addLayer(new HeadLayer<>(this));
    this.addLayer(new ElytraLayer<>(this));
    this.addLayer(new BeeStingerLayer<>(this));
  }

  @Override
  public ResourceLocation getEntityTexture(MiniMeEntity entity) {
    ResourceLocation loc = entity.getSkinResourceLocation();
    return loc == null ? TEXTURE_STEVE : loc;
  }

  public static class Factory implements IRenderFactory<MiniMeEntity> {
    @Override
    @Nonnull
    public MiniMeRenderer createRenderFor(@Nonnull EntityRendererManager manager) {
      return new MiniMeRenderer(manager, ModelHolder.miniMe, 0.5f);
    }
  }
}
