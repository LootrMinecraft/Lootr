package noobanidus.mods.lootr.common.impl.type;

import com.google.auto.service.AutoService;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.ILootrType;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrType.class)
public class MinecartLootrType implements ILootrType {
  @Override
  public String getName() {
    return BuiltInLootrTypes.TYPE_MINECART;
  }

  @Override
  public @Nullable Block getReplacementBlock() {
    return null;
  }

  @Override
  public @Nullable EntityType<?> getReplacementEntity() {
    return EntityType.CHEST_MINECART;
  }

  @Override
  public void callback() {
    BuiltInLootrTypes.MINECART = this;
  }

  @Override
  public boolean isEntity() {
    return true;
  }
}
