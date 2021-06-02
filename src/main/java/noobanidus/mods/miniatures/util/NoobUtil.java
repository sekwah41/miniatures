package noobanidus.mods.miniatures.util;

import noobanidus.mods.miniatures.entity.MiniMeEntity;

import java.util.UUID;

public class NoobUtil {
  private static UUID nooblybear = UUID.fromString("fcbee488-f5a5-490e-97b5-7e6ed870e06c");
  private static UUID noobanidus = UUID.fromString("9902e63b-d02d-4689-8c5c-9653f45cc268");

  public static boolean isNoob (MiniMeEntity entity) {
    if (entity.getGameProfile().isPresent()) {
      UUID profileId = entity.getGameProfile().get().getId();
      return profileId != null && (profileId.equals(nooblybear) || profileId.equals(noobanidus));
    }

    return false;
  }
}
