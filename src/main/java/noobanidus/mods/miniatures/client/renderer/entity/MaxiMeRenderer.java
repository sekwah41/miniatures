package noobanidus.mods.miniatures.client.renderer.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import noobanidus.mods.miniatures.client.ModelHolder;
import noobanidus.mods.miniatures.client.model.MiniMeModel;
import noobanidus.mods.miniatures.entity.MiniMeEntity;

import javax.annotation.Nonnull;
import java.util.Map;

public class MaxiMeRenderer extends BipedRenderer<MiniMeEntity, MiniMeModel<MiniMeEntity>> {
  private static final ResourceLocation TEXTURE_STEVE = new ResourceLocation("textures/entity/steve.png");

  public MaxiMeRenderer(EntityRendererManager renderManager, MiniMeModel model, float shadow) {
    super(renderManager, model, shadow);
    this.addLayer(new HeldItemLayer<>(this));
    this.addLayer(new ArrowLayer<>(this));
    this.addLayer(new HeadLayer<>(this));
    this.addLayer(new ElytraLayer<>(this));
    this.addLayer(new BeeStingerLayer<>(this));
    this.addLayer(new BipedArmorLayer<>(this, new BipedModel(1.02F), new BipedModel(1.02F)));
    //this.addLayer(new BipedArmorLayer<>(this, new MiniMeModel(5.0f, model.isArms()), new MiniMeModel(5.0f, model.isArms())));
  }

  @Override
  public ResourceLocation getEntityTexture(MiniMeEntity entity) {
    return entity.getGameProfile()
        .map(this::getSkin)
        .orElse(TEXTURE_STEVE);
  }

  private ResourceLocation getSkin(GameProfile gameProfile) {
    if (!gameProfile.isComplete()) {
      return TEXTURE_STEVE;
    } else {
      final Minecraft minecraft = Minecraft.getInstance();
      SkinManager skinManager = minecraft.getSkinManager();
      final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> loadSkinFromCache = skinManager.loadSkinFromCache(gameProfile); // returned map may or may not be typed
      if (loadSkinFromCache.containsKey(MinecraftProfileTexture.Type.SKIN)) {
        return skinManager.loadSkin(loadSkinFromCache.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
      } else {
        return DefaultPlayerSkin.getDefaultSkin(gameProfile.getId());
      }
    }
  }

  @Override
  public void render(MiniMeEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
    this.entityModel = ModelHolder.miniMe;
    if (entityIn.isSlim() && this.entityModel != ModelHolder.miniMeSlim) {
      this.entityModel = ModelHolder.miniMeSlim;
    }
    super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
  }

  protected void preRenderCallback(MiniMeEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    matrixStackIn.scale(3.9375F, 3.9375F, 3.9375F);
  }

  public static class Factory implements IRenderFactory<MiniMeEntity> {
    @Override
    @Nonnull
    public MaxiMeRenderer createRenderFor(@Nonnull EntityRendererManager manager) {
      return new MaxiMeRenderer(manager, ModelHolder.miniMe, 0.5f);
    }
  }
}
