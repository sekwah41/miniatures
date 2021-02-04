package noobanidus.mods.miniatures.init;

import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import noobanidus.mods.miniatures.MiniTags;

import static noobanidus.mods.miniatures.Miniatures.REGISTRATE;

public class ModTags {
  static {
    REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, (p) -> {
      p.getOrCreateBuilder(MiniTags.Blocks.BREAK_BLOCKS).add(Blocks.TORCH);
      p.getOrCreateBuilder(MiniTags.Blocks.BREAK_BLOCKS).addTag(BlockTags.FLOWERS);
    });
  }

  public static void load () {
  }
}
