package noobanidus.mods.miniatures.client.model;

import noobanidus.mods.miniatures.client.AdditionalRenderTypes;
import noobanidus.mods.miniatures.entity.MiniMeEntity;

public class GhostlyMiniMeModel<E extends MiniMeEntity> extends MiniMeModel<E> {
  public GhostlyMiniMeModel(float modelSize, boolean arms) {
    super(AdditionalRenderTypes::getSpiritEntity, modelSize, arms);
  }
}
