package noobanidus.mods.miniatures.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import noobanidus.mods.miniatures.entity.MiniMeEntity;

public class MiniMeModel extends PlayerModel<MiniMeEntity> {

  public MiniMeModel(float modelSize, boolean arms) {
    super(modelSize, arms);
  }

  @Override
  public void setRotationAngles(MiniMeEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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
