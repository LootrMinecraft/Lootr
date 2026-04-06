package noobanidus.mods.lootr.common.impl.accessor;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.common.api.interfaces.accessor.ILootrItemFrameAccessor;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinItemFrame;

@AutoService(ILootrItemFrameAccessor.class)
public class ItemFrameAccessor implements ILootrItemFrameAccessor<ItemFrame> {
  @Override
  public Class<ItemFrame> getAssignableClass() {
    return ItemFrame.class;
  }

  @Override
  public ItemStack getItem(ItemFrame entity) {
    return entity.getItem();
  }

  @Override
  public int getRotation(ItemFrame object) {
    return object.getRotation();
  }

  @Override
  public Direction getDirection(ItemFrame entity) {
    return entity.getDirection();
  }

  @Override
  public BlockPos getPos(ItemFrame entity) {
    return entity.getPos();
  }

  @Override
  public boolean isFixed(ItemFrame object) {
    return ((AccessorMixinItemFrame)object).lootr$isFixed();
  }

  @Override
  public boolean isInvisible(ItemFrame object) {
    return object.isInvisible();
  }
}
