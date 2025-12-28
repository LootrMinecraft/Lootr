package noobanidus.mods.lootr.common.impl.type;

import com.google.auto.service.AutoService;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.ILootrType;
import noobanidus.mods.lootr.common.api.data.ILootrInfo;
import org.jetbrains.annotations.Nullable;

// TODO: I'm not sure if this is the correct solution
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
  public @Nullable Container getContainer(ILootrInfo info, ServerLevel level) {
    if (level.getBlockEntity(info.getInfoPos()) instanceof Container container) {
      return container;
    }

    return null;
  }
}
