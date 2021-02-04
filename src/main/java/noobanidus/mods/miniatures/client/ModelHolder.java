package noobanidus.mods.miniatures.client;

import noobanidus.mods.miniatures.client.model.MiniMeModel;

public class ModelHolder {
  public static MiniMeModel miniMe;

  public static void init () {
    miniMe = new MiniMeModel(0.5f, false);
  }
}
