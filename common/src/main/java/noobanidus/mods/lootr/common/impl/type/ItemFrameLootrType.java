package noobanidus.mods.lootr.common.impl.type;

import com.google.auto.service.AutoService;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.type.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrType.class)
public class ItemFrameLootrType implements ILootrType {
  @Override
  public String getName() {
    return BuiltInLootrTypes.TYPE_ITEM_FRAME;
  }

  @Override
  public @Nullable Block getReplacementBlock() {
    return null;
  }

  @Override
  public @Nullable EntityType<?> getReplacementEntity() {
    return LootrRegistry.getItemFrame();
  }

  @Override
  public void callback() {
    BuiltInLootrTypes.ITEM_FRAME = this;
  }

  @Override
  public boolean isEntity() {
    return true;
  }

  @Override
  public boolean canDecay() {
    return false;
  }

  @Override
  public boolean canRefresh() {
    return false;
  }
}
