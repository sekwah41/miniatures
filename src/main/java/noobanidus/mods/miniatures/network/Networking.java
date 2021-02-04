package noobanidus.mods.miniatures.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import noobanidus.libs.noobutil.network.PacketHandler;
import noobanidus.mods.miniatures.Miniatures;

public class Networking extends PacketHandler {
  public static Networking INSTANCE = new Networking();

  public Networking() {
    super(Miniatures.MODID);
  }

  @Override
  public void registerMessages() {
    // Server -> Client
    registerMessage(OwnerChanged.class, OwnerChanged::encode, OwnerChanged::new, OwnerChanged::handle);
  }

  public static void sendTo(Object msg, ServerPlayerEntity player) {
    INSTANCE.sendToInternal(msg, player);
  }

  public static void sendToServer(Object msg) {
    INSTANCE.sendToServerInternal(msg);
  }

  public static <MSG> void send(PacketDistributor.PacketTarget target, MSG message) {
    INSTANCE.sendInternal(target, message);
  }
}
