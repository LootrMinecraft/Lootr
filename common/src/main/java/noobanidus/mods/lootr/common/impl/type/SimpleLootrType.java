package noobanidus.mods.lootr.common.impl.type;

import com.google.auto.service.AutoService;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;
import noobanidus.mods.lootr.common.api.data.ILootrData;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrType.class)
public class SimpleLootrType implements ILootrType {
  @Override
  public String getName() {
    return BuiltInLootrTypes.TYPE_SIMPLE;
  }

  @Override
  public @Nullable Block getReplacementBlock() {
    return null;
  }

  @Override
  public @Nullable EntityType<?> getReplacementEntity() {
    return null;
  }

  @Override
  public void callback() {
    BuiltInLootrTypes.SIMPLE = this;
  }

  @Override
  public @Nullable Container getContainer(ILootrData info, ServerLevel level) {
    if (level.getBlockEntity(info.getDataPos()) instanceof Container container) {
      return container;
    }

    return null;
  }
}
