package noobanidus.mods.lootr.neoforge.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;

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
    if (entity.getType().is(LootrTags.Entity.CONVERT_ENTITIES)) {
      ILootrDataAdapter<Entity> adapter = LootrAPI.findAdapter(entity);
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

      var executor = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
      executor.tell(new TickTask(0, () -> event.getLevel().addFreshEntity(lootrCart)));
    }
  }
}
