package noobanidus.mods.miniatures.client;

import noobanidus.mods.miniatures.client.model.MiniMeModel;

public class ModelHolder {
  public static MiniMeModel miniMe;
  public static MiniMeModel miniMeSlim;
  public static MiniMeModel torso;
  public static MiniMeModel legs;

  public static void init () {
    miniMe = new MiniMeModel(0.5f, false);
    miniMeSlim = new MiniMeModel(0.5f, true);
    torso = new MiniMeModel(0.25f, false);
    legs = new MiniMeModel(0.5f, false);
  }
}
