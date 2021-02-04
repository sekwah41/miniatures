package noobanidus.mods.miniatures;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class MiniTags {
  public static class Blocks extends MiniTags {
    public static Tags.IOptionalNamedTag<Block> BREAK_BLOCKS = modTag("break_blocks");

    static Tags.IOptionalNamedTag<Block> modTag(String name) {
      return BlockTags.createOptional(new ResourceLocation(Miniatures.MODID, name));
    }

    static Tags.IOptionalNamedTag<Block> compatTag(String name) {
      return BlockTags.createOptional(new ResourceLocation("forge", name));
    }
  }
}
