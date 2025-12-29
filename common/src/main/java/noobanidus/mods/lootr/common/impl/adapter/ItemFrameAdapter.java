package noobanidus.mods.lootr.common.impl.adapter;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import noobanidus.mods.lootr.common.api.adapter.ILootrItemFrameAdapter;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinItemFrame;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrItemFrameAdapter.class)
public class ItemFrameAdapter implements ILootrItemFrameAdapter<ItemFrame> {
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
