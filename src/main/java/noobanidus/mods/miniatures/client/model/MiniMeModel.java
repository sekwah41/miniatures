package noobanidus.mods.miniatures.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import noobanidus.mods.miniatures.client.AdditionalRenderTypes;
import noobanidus.mods.miniatures.entity.MiniMeEntity;

import java.util.function.Function;

public class MiniMeModel<E extends MiniMeEntity> extends PlayerRenderModel<E> {

  private final boolean arms;

  public MiniMeModel(float modelSize, boolean arms) {
    this(AdditionalRenderTypes::getEntityTranslucent, modelSize, arms);
  }

  public MiniMeModel(Function<ResourceLocation, RenderType> renderTypeIn, float modelSize, boolean smallArmsIn) {
    this(renderTypeIn, modelSize, 0.0F, 64, 64, smallArmsIn);
  }

  public MiniMeModel(Function<ResourceLocation, RenderType> renderTypeIn, float modelSize, float yOffsetIn, int textureWidthIn, int textureHeightIn, boolean smallArmsIn) {
    super(renderTypeIn, modelSize, yOffsetIn, textureWidthIn, textureHeightIn, smallArmsIn);
    this.arms = smallArmsIn;
  }

  public boolean isArms() {
    return arms;
  }

  @Override
  public void setRotationAngles(E entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    int noob = entityIn.getNoobVariant();
    if (noob == 1) {
      this.bipedLeftLeg.rotateAngleX = 0.0f;
      this.bipedLeftLeg.rotateAngleZ = 0.0f;
      this.bipedRightLeg.rotateAngleX = 0.0f;
      this.bipedRightLeg.rotateAngleZ = 0.0f;
    }
    if (entityIn.isBeingRidden()) {
      this.bipedLeftArm.rotateAngleX = -3f;
      this.bipedRightArm.rotateAngleX = -3f;
      this.bipedLeftArm.rotateAngleZ = 0.3f;
      this.bipedRightArm.rotateAngleZ = -0.3f;
    }
    this.bipedHeadwear.copyModelAngles(this.bipedHead);
    this.bipedBodyWear.copyModelAngles(this.bipedBody);
    this.bipedLeftArmwear.copyModelAngles(this.bipedLeftArm);
    this.bipedRightArmwear.copyModelAngles(this.bipedRightArm);
    this.bipedLeftLegwear.copyModelAngles(this.bipedLeftLeg);
    this.bipedRightLegwear.copyModelAngles(this.bipedRightLeg);
  }

  @Override
  protected Iterable<ModelRenderer> getHeadParts() {
    return ImmutableList.of(this.bipedHead, this.bipedHeadwear);
  }


}
