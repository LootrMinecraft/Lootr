package noobanidus.mods.lootr.fabric.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.entity.EntityTicker;
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
    if (entity.getType().is(LootrTags.Entity.CONVERT_ENTITIES) && entity instanceof AbstractMinecartContainer cart) {
      if (cart.getLootTable() != null && !LootrAPI.isLootTableBlacklisted(cart.getLootTable())) {
        LootrChestMinecartEntity lootrCart = new LootrChestMinecartEntity(cart.level(), cart.getX(), cart.getY(), cart.getZ());
        PlatformAPI.copyEntityData(cart, lootrCart);
        cir.setReturnValue(false);
        cir.cancel();
        if (!level.getServer().isSameThread()) {
          level.getChunkSource().addRegionTicket(LootrAPI.LOOTR_ENTITY_TICK_TICKET, lootrCart.chunkPosition(), 1, Unit.INSTANCE);
          LootrAPI.LOG.error("Minecart with Loot table created off main thread. Falling back on EntityTicker.");
          EntityTicker.addEntity(lootrCart);
        } else {
          level.addFreshEntity(lootrCart);
        }
      }
    }
  }
}
