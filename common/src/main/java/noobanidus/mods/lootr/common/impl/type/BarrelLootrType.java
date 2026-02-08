package noobanidus.mods.lootr.common.impl.type;

import com.google.auto.service.AutoService;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import noobanidus.mods.lootr.common.api.type.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrType.class)
public class BarrelLootrType implements ILootrType {
  @Override
  public String getName() {
    return BuiltInLootrTypes.TYPE_BARREL;
  }

  @Override
  public @Nullable Block getReplacementBlock() {
    return Blocks.BARREL;
  }

  @Override
  public @Nullable EntityType<?> getReplacementEntity() {
    return null;
  }

  @Override
  public void callback() {
    BuiltInLootrTypes.BARREL = this;
  }
}
