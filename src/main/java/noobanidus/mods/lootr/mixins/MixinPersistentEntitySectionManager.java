package noobanidus.mods.lootr.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.entity.EntityTicker;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Equivalent to HandleCart::onEntityJoin
@Mixin(PersistentEntitySectionManager.class)
public class MixinPersistentEntitySectionManager {
  @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
  private void LootrAddEntity(EntityAccess entityAccess, boolean bl, CallbackInfoReturnable<Boolean> cir) {
    if (ConfigManager.get().conversion.disable) {
      return;
    }
    if (entityAccess instanceof Entity entity && entity.getType() == EntityType.CHEST_MINECART) {
      if (ConfigManager.isDimensionBlocked(entity.level().dimension())) {
        return;
      }
      MinecartChest chest = (MinecartChest) entity;
      if (!chest.level().isClientSide && chest.lootTable != null && !ConfigManager.getLootBlacklist().contains(chest.lootTable)) {
        if (chest.level() instanceof ServerLevel level) {
          LootrChestMinecartEntity lootr = new LootrChestMinecartEntity(chest.level(), chest.getX(), chest.getY(), chest.getZ());
          lootr.setLootTable(chest.lootTable, chest.lootTableSeed);
          cir.setReturnValue(false);
          cir.cancel();
          if (level.getServer().isSameThread()) {
            chest.level().addFreshEntity(lootr);
          } else {
            EntityTicker.addEntity(lootr);
          }
        }
      }
    }
  }
}
