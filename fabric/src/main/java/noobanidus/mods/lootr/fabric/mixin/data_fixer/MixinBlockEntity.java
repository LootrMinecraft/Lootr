package noobanidus.mods.lootr.fabric.mixin.data_fixer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.LootrConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// TODO: Remove these migrations
@Mixin(BlockEntity.class)
public class MixinBlockEntity {
  @WrapOperation(method = "loadStatic", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;tryParse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
  private static ResourceLocation LootrLoadStatic(String string, Operation<ResourceLocation> original) {
    return switch (string) {
      case LootrConstants.LOOTR_SPECIAL_CHEST -> LootrConstants.LOOTR_CHEST;
      case LootrConstants.LOOTR_SPECIAL_BARREL -> LootrConstants.LOOTR_BARREL;
      case LootrConstants.LOOTR_SPECIAL_TRAPPED_CHEST -> LootrConstants.LOOTR_TRAPPED_CHEST;
      case LootrConstants.LOOTR_SPECIAL_SHULKER -> LootrConstants.LOOTR_SHULKER;
      case LootrConstants.LOOTR_SPECIAL_INVENTORY -> LootrConstants.LOOTR_INVENTORY;
      default -> original.call(string);
    };
  }
}
