package noobanidus.mods.miniatures.client;

import noobanidus.mods.miniatures.client.model.GhostlyMiniMeModel;
import noobanidus.mods.miniatures.client.model.GlowingMiniMeModel;
import noobanidus.mods.miniatures.client.model.MiniMeModel;

public class ModelHolder {
  public static MiniMeModel miniMe;
  public static MiniMeModel miniMeSlim;
  public static MiniMeModel miniMeGhost;
  public static MiniMeModel miniMeGhostSlim;
  public static MiniMeModel miniMeGlow;
  public static MiniMeModel miniMeGlowSlim;

  public static void init () {
    miniMe = new MiniMeModel(0.5f, false);
    miniMeSlim = new MiniMeModel(0.5f, true);
    miniMeGhost = new GhostlyMiniMeModel(0.5f, false);
    miniMeGhostSlim = new GhostlyMiniMeModel(0.5f, true);
    miniMeGlow = new GlowingMiniMeModel(0.5f, false);
    miniMeGlowSlim = new GlowingMiniMeModel(0.5f, true);
  }
}
