package noobanidus.mods.lootr.common.impl.type;

import com.google.auto.service.AutoService;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrType.class)
public class GravelLootrType extends BrushableLootrType {
  @Override
  public String getName() {
    return BuiltInLootrTypes.TYPE_GRAVEL;
  }

  @Override
  public @Nullable Block getReplacementBlock() {
    return Blocks.GRAVEL;
  }

  @Override
  public void callback() {
    BuiltInLootrTypes.GRAVEL = this;
  }
}
