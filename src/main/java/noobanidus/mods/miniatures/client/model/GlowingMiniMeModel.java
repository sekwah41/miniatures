package noobanidus.mods.miniatures.client.model;

import noobanidus.mods.miniatures.client.AdditionalRenderTypes;
import noobanidus.mods.miniatures.entity.MiniMeEntity;

public class GlowingMiniMeModel<E extends MiniMeEntity> extends MiniMeModel<E> {
  public GlowingMiniMeModel(float modelSize, boolean arms) {
    super(AdditionalRenderTypes::getGlowing, modelSize, arms);
  }
}
