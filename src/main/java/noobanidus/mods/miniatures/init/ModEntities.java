package noobanidus.mods.miniatures.init;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.loot.LootTable;
import noobanidus.mods.miniatures.entity.MaxiMeEntity;
import noobanidus.mods.miniatures.entity.MiniMeEntity;

import static noobanidus.mods.miniatures.Miniatures.REGISTRATE;

public class ModEntities {
  public static RegistryEntry<EntityType<MiniMeEntity>> MINIME = REGISTRATE.<MiniMeEntity>entity("minime", MiniMeEntity::new, EntityClassification.CREATURE)
      .loot((p, e) -> p.registerLootTable(e, LootTable.builder()))
      .properties(o -> o.size(0.6F, 2.2F).trackingRange(8).setShouldReceiveVelocityUpdates(true).setUpdateInterval(4))
      .register();

  public static RegistryEntry<EntityType<MaxiMeEntity>> MAXIME = REGISTRATE.<MaxiMeEntity>entity("maxime", MaxiMeEntity::new, EntityClassification.CREATURE)
      .loot((p, e) -> p.registerLootTable(e, LootTable.builder()))
      .properties(o -> o.size(2.3f, 7).trackingRange(16).setShouldReceiveVelocityUpdates(true).setUpdateInterval(4))
      .register();

  public static void load() {
  }

  public static void registerEntities() {
    GlobalEntityTypeAttributes.put(ModEntities.MINIME.get(), MiniMeEntity.attributes().create());
    GlobalEntityTypeAttributes.put(ModEntities.MAXIME.get(), MaxiMeEntity.attributes().create());
  }
}
