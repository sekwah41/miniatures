package noobanidus.mods.miniatures.client.model;

import net.minecraft.client.renderer.entity.model.PlayerModel;
import noobanidus.mods.miniatures.entity.MiniMeEntity;

public class MiniMeModel extends PlayerModel<MiniMeEntity> {

  public MiniMeModel(float modelSize, boolean smallArmsIn) {
    super(modelSize, false);
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
  }
}
