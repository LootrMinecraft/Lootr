package noobanidus.mods.lootr.common.impl;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.adapter.ILootrItemFrameAdapter;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;

public class ItemFrameHandler {
  public static void maybeReplaceItemFrames(ServerLevelAccessor level, Entity entity, Operation<Void> original) {
    if (LootrAPI.shouldConvertStructureItemFrames() && entity.getType().is(LootrTags.Entity.CONVERT_ITEM_FRAMES)) {
      ILootrItemFrameAdapter<Entity> adapter = LootrAPI.getItemFrameAdapter(entity);
      if (adapter != null && !adapter.isFixed(entity) && !adapter.isInvisible(entity)) {
        ItemStack contained = adapter.getItem(entity);
        if (!contained.isEmpty() && !contained.is(LootrTags.Items.ITEM_FRAME_CONVERT_BLACKLIST)) {
          LootrItemFrame newItemFrame = new LootrItemFrame(level.getLevel(), adapter.getPos(entity), adapter.getDirection(entity));
          newItemFrame.lootrSetItem(contained);
          PlatformAPI.copyEntityData(adapter, entity, newItemFrame);

          level.addFreshEntityWithPassengers(newItemFrame);
          return;
        }
      }
    }
    original.call(level, entity);
  }
}
