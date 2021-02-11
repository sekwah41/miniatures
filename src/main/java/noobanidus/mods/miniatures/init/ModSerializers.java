package noobanidus.mods.miniatures.init;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ModSerializers {
  public static final IDataSerializer<Optional<GameProfile>> OPTIONAL_GAME_PROFILE = new IDataSerializer<Optional<GameProfile>>() {
    @Override
    public void write(@Nonnull PacketBuffer packetBuffer, @Nonnull Optional<GameProfile> gameProfile) {
      if (gameProfile.isPresent()) {
        packetBuffer.writeBoolean(true);
        packetBuffer.writeCompoundTag(NBTUtil.writeGameProfile(new CompoundNBT(), gameProfile.get()));
      } else {
        packetBuffer.writeBoolean(false);
      }
    }

    @Override
    @Nonnull
    public Optional<GameProfile> read(@Nonnull PacketBuffer packetBuffer) {
      if (packetBuffer.readBoolean()) {
        CompoundNBT tag = packetBuffer.readCompoundTag();
        if (tag != null) {
          GameProfile profile = NBTUtil.readGameProfile(tag);
          if (profile != null) {
            return Optional.of(profile);
          }
        }
      }
      return Optional.empty();
    }

    @Override
    @Nonnull
    public Optional<GameProfile> copyValue(@Nonnull Optional<GameProfile> gameProfile) {
      return gameProfile;
    }
  };

  public static void load() {
  }
}
