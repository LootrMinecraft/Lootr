package noobanidus.mods.lootr.fabric.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Equivalent to HandleCart::onEntityJoin
@Mixin(PersistentEntitySectionManager.class)
public class MixinPersistentEntitySectionManager {
  @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
  private void LootrAddEntity(EntityAccess entityAccess, boolean bl, CallbackInfoReturnable<Boolean> cir) {
    if (LootrAPI.isDisabled()) {
      return;
    }
    if (!(entityAccess instanceof Entity entity)) {
      return;
    }
    if (!(entity.level() instanceof ServerLevel level) || level.isClientSide()) {
      return;
    }
    if (LootrAPI.isDimensionBlocked(level.dimension())) {
      return;
    }
    if (entity.getType().is(LootrTags.Entity.CONVERT_ENTITIES)) {
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

      LootrAPI.postProcess(level, lootrCart, lootTable, seed);

      cir.setReturnValue(false);
      cir.cancel();
      level.getServer().tell(new TickTask(0, () -> level.addFreshEntity(lootrCart)));
    }
  }
}
