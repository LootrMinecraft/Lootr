package net.zestyblaze.lootr.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.entity.EntityTicker;
import net.zestyblaze.lootr.entity.LootrChestMinecartEntity;
import net.zestyblaze.lootr.registry.LootrEntityInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Equivalent to HandleCart::onEntityJoin
@Mixin(PersistentEntitySectionManager.class)
public class MixinPersistentEntitySectionManager {
  @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
  private void LootrAddEntity(EntityAccess entityAccess, boolean bl, CallbackInfoReturnable<Boolean> cir) {
    if (entityAccess instanceof Entity entity && entity.getType() == EntityType.CHEST_MINECART) {
      MinecartChest chest = (MinecartChest) entity;
      if (!chest.level.isClientSide && chest.lootTable != null && LootrModConfig.get().conversion.convert_mineshafts && !LootrModConfig.getLootBlacklist().contains(chest.lootTable)) {
        LootrChestMinecartEntity lootr = new LootrChestMinecartEntity(LootrEntityInit.LOOTR_MINECART_ENTITY, chest.getX(), chest.getY(), chest.getZ(), chest.level);
        lootr.setLootTable(chest.lootTable, chest.lootTableSeed);
        cir.setReturnValue(false);
        cir.cancel();
        EntityTicker.addEntity(lootr);
      }
    }
  }
}
