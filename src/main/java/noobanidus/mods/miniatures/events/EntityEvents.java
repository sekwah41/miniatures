package noobanidus.mods.miniatures.events;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.miniatures.Miniatures;
import noobanidus.mods.miniatures.entity.MiniMeEntity;
import noobanidus.mods.miniatures.init.ModEntities;

@Mod.EventBusSubscriber(modid = Miniatures.MODID)
public class EntityEvents {
  @SubscribeEvent
  public static void onSizeChange(EntityEvent.Size event) {
    if (event.getEntity().isAddedToWorld()) {
      EntityType type = event.getEntity().getType();
      if (type == ModEntities.MINIME.get() || type == ModEntities.MAXIME.get() || type == ModEntities.ME.get()) {
        MiniMeEntity mini = (MiniMeEntity) event.getEntity();
        EntitySize oldSize = event.getOldSize();
        event.setNewSize(EntitySize.flexible(oldSize.width * mini.getScale(), oldSize.height * mini.getScale()));
        event.setNewEyeHeight(event.getOldEyeHeight() * mini.getScale());
      }
    }
  }

  // TODO: Only reset the pick-up delay when entity dismounts
}
