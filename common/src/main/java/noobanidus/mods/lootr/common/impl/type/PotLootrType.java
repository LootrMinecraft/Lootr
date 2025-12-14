package noobanidus.mods.lootr.common.impl.type;

import com.google.auto.service.AutoService;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.ILootrType;
import noobanidus.mods.lootr.common.block.entity.LootrDecoratedPotBlockEntity;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrType.class)
public class PotLootrType implements ILootrType {
  @Override
  public String getName() {
    return BuiltInLootrTypes.TYPE_POT;
  }

  @Override
  public @Nullable Block getReplacementBlock() {
    return Blocks.DECORATED_POT;
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
  public boolean canRefresh() {
    return false;
  }

  @Override
  public void callback() {
    BuiltInLootrTypes.POT = this;
  }
}
