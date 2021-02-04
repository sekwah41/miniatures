package noobanidus.mods.miniatures.util;

import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class GameProfileSerializer {
  public static void write(GameProfile o, PacketBuffer output) {
    final UUID uuid = o.getId();
    if (uuid == null) {
      output.writeBoolean(false);
    } else {
      output.writeBoolean(true);
      output.writeUniqueId(uuid);
    }
    output.writeString(Strings.nullToEmpty(o.getName()));
    final PropertyMap properties = o.getProperties();
    output.writeVarInt(properties.size());
    for (Property p : properties.values()) {
      output.writeString(p.getName());
      output.writeString(p.getValue());

      final String signature = p.getSignature();
      if (signature != null) {
        output.writeBoolean(true);
        output.writeString(signature);
      } else {
        output.writeBoolean(false);
      }
    }
  }

  public static GameProfile read(PacketBuffer input) {
    boolean hasUuid = input.readBoolean();
    UUID uuid = hasUuid ? input.readUniqueId() : null;
    final String name = input.readString(Short.MAX_VALUE);
    GameProfile result = new GameProfile(uuid, name);
    int propertyCount = input.readVarInt();

    final PropertyMap properties = result.getProperties();
    for (int i = 0; i < propertyCount; ++i) {
      String key = input.readString(Short.MAX_VALUE);
      String value = input.readString(Short.MAX_VALUE);
      if (input.readBoolean()) {
        String signature = input.readString(Short.MAX_VALUE);
        properties.put(key, new Property(key, value, signature));
      } else {
        properties.put(key, new Property(key, value));
      }

    }

    return result;
  }
}
