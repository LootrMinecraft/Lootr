package net.zestyblaze.lootr.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Equivalent to HandleCart::onEntityJoin
@Mixin(PersistentEntitySectionManager.class)
public class MixinPersistentEntitySectionManager {
  @Inject(method="addNewEntity", at=@At("HEAD"), cancellable = true)
  private static void LootrAddNewEntity(EntityAccess entityAccess, CallbackInfoReturnable<Boolean> cir) {
    if(entityAccess instanceof Entity entity && entity.getType() == EntityType.CHEST_MINECART) {
      // TODO: Handle minecarts properly
      cir.cancel();
    }
  }
}
