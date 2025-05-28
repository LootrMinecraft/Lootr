package noobanidus.mods.lootr.common.api.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Locale;

public enum LootrBlockType implements StringRepresentable {
  CHEST(Blocks.CHEST),
  TRAPPED_CHEST(Blocks.TRAPPED_CHEST),
  BARREL(Blocks.BARREL),
  SHULKER(Blocks.SHULKER_BOX),
  INVENTORY(Blocks.CHEST),
  ENTITY(Blocks.AIR);

  public static final Codec<LootrBlockType> CODEC = StringRepresentable.fromEnum(LootrBlockType::values);

  private final Block block;

  LootrBlockType(Block block) {
    this.block = block;
  }

  public Block getBlock () {
    return block;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }
}
