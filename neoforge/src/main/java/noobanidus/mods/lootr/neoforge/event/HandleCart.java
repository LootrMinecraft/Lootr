package noobanidus.mods.lootr.neoforge.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import noobanidus.mods.lootr.common.api.adapter.ILootrItemFrameAdapter;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;

// TODO: Abstract common code out of here
@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleCart {
  @SubscribeEvent
  public static void onEntityJoin(EntityJoinLevelEvent event) {
    if (!(event.getLevel() instanceof ServerLevel level) || level.isClientSide()) {
      return;
    }
    if (LootrAPI.isDimensionBlocked(level.dimension()) || LootrAPI.isDisabled()) {
      return;
    }
    Entity entity = event.getEntity();
    if (LootrAPI.shouldConvertStructureItemFrames() && entity.getType()
        .is(LootrTags.Entity.CONVERT_ITEM_FRAMES) && entity.getTags()
        .contains(LootrConstants.CAN_CONVERT_TAG) /* Item frames without CAN_CONVERT_TAG handled elsewhere */) {
      ILootrItemFrameAdapter<Entity> adapter = LootrAPI.getItemFrameAdapter(entity);
      if (adapter == null) {
        LootrAPI.LOG.error("No item frame adapter found for entity '{}' even though it is tagged for conversion.", entity);
        return;
      }

      if (!adapter.isFixed(entity) && !adapter.isInvisible(entity)) {
        ItemStack contained = adapter.getItem(entity);
        if (!contained.isEmpty() && !contained.is(LootrTags.Items.ITEM_FRAME_CONVERT_BLACKLIST)) {
          LootrItemFrame newItemFrame = new LootrItemFrame(level.getLevel(), adapter.getPos(entity), adapter.getDirection(entity));
          newItemFrame.lootrSetItem(contained);
          PlatformAPI.copyEntityData(adapter, entity, newItemFrame);
          event.setCanceled(true);

          // TODO: Processing
          level.getServer().execute(() -> event.getLevel().addFreshEntity(newItemFrame));
        }
      }
    } else if (entity.getType().is(LootrTags.Entity.CONVERT_ENTITIES) && !entity.getType().is(LootrTags.Entity.CONVERT_BLACKLIST)) {
      ILootrDataAdapter<Entity> adapter = LootrAPI.getAdapter(entity);
      if (adapter == null) {
        LootrAPI.LOG.error("No adapter found for entity '{}' even though it is tagged for conversion.", entity);
        return;
      }
      ResourceKey<LootTable> lootTable = adapter.getLootTable(entity);
      if (lootTable == null || LootrAPI.isLootTableBlacklisted(lootTable)) {
        return;
      }
      long seed = adapter.getLootSeed(entity);

      LootrAPI.preProcess(level, entity, lootTable, seed);

      LootrChestMinecartEntity lootrCart = new LootrChestMinecartEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ());
      PlatformAPI.copyEntityData(adapter, entity, lootrCart);
      event.setCanceled(true);

      LootrAPI.postProcess(level, lootrCart, lootTable, seed);

      level.getServer().execute(() -> event.getLevel().addFreshEntity(lootrCart));
    }
  }
}
