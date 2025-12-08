package noobanidus.mods.lootr.common.impl.type;

import com.google.auto.service.AutoService;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.ILootrType;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrType.class)
public class SandLootrType implements ILootrType {
  @Override
  public String getName() {
    return BuiltInLootrTypes.TYPE_SAND;
  }

  @Override
  public @Nullable Block getReplacementBlock() {
    return Blocks.SAND;
  }

  @Override
  public @Nullable EntityType<?> getReplacementEntity() {
    return null;
  }

  @Override
  public boolean canDecay() {
    return false;
  }

  @Override
  public void callback() {
    BuiltInLootrTypes.SAND = this;
  }
}
