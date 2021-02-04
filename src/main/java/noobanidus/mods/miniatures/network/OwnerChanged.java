package noobanidus.mods.miniatures.network;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import noobanidus.mods.miniatures.entity.MiniMeEntity;
import noobanidus.mods.miniatures.util.GameProfileSerializer;

import java.util.function.Supplier;

public class OwnerChanged {
  public GameProfile profile;
  public int entityId;

  public OwnerChanged (PacketBuffer input) {
    this.entityId = input.readVarInt();
    if (input.readBoolean()) {
      profile = GameProfileSerializer.read(input);
    }
  }

  public OwnerChanged(int entityId, GameProfile profile) {
    this.profile = profile;
    this.entityId = entityId;
  }

  public void encode (PacketBuffer output) {
    output.writeVarInt(entityId);

    if (profile != null) {
      output.writeBoolean(true);
      GameProfileSerializer.write(profile, output);
    } else {
      output.writeBoolean(false);
    }
  }

  public void handle (Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> handle(this, context));
  }

  @OnlyIn(Dist.CLIENT)
  private static void handle (OwnerChanged message, Supplier<NetworkEvent.Context> context) {
    World world = Minecraft.getInstance().world;
    if (world != null) {
      Entity e = world.getEntityByID(message.entityId);
      if (e instanceof MiniMeEntity) {
        ((MiniMeEntity) e).setOwner(message.profile);
      }
    }
    context.get().setPacketHandled(true);
  }
}
