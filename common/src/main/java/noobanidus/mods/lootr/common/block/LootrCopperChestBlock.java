package noobanidus.mods.lootr.common.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import noobanidus.mods.lootr.common.api.LootrRegistry;

public class LootrCopperChestBlock extends LootrChestBlock {
  public static final CopperType COPPER = new CopperType(SoundEvents.COPPER_CHEST_OPEN, SoundEvents.COPPER_CHEST_CLOSE);
  public static final CopperType EXPOSED = new CopperType(SoundEvents.COPPER_CHEST_OPEN, SoundEvents.COPPER_CHEST_CLOSE);
  public static final CopperType WEATHERED = new CopperType(SoundEvents.COPPER_CHEST_WEATHERED_OPEN, SoundEvents.COPPER_CHEST_WEATHERED_CLOSE);
  public static final CopperType OXIDIZED = new CopperType(SoundEvents.COPPER_CHEST_OXIDIZED_OPEN, SoundEvents.COPPER_CHEST_OXIDIZED_CLOSE);

  public LootrCopperChestBlock(CopperType type, Properties properties) {
    super(LootrRegistry::getChestBlockEntity, type.open(), type.close(), properties);
  }

  public record CopperType(SoundEvent open, SoundEvent close) {
  }
}
