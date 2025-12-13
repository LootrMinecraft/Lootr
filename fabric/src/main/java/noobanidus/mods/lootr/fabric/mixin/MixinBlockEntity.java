package noobanidus.mods.lootr.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.registry.LootrProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// TODO: Remove these migrations
@Mixin(BlockEntity.class)
public class MixinBlockEntity {
  @WrapOperation(method = "loadStatic", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;tryParse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
  private static ResourceLocation LootrLoadStatic(String string, Operation<ResourceLocation> original) {
    return switch (string) {
      case LootrProperties.LOOTR_SPECIAL_CHEST -> LootrProperties.LOOTR_CHEST;
      case LootrProperties.LOOTR_SPECIAL_BARREL -> LootrProperties.LOOTR_BARREL;
      case LootrProperties.LOOTR_SPECIAL_TRAPPED_CHEST -> LootrProperties.LOOTR_TRAPPED_CHEST;
      case LootrProperties.LOOTR_SPECIAL_SHULKER -> LootrProperties.LOOTR_SHULKER;
      case LootrProperties.LOOTR_SPECIAL_INVENTORY -> LootrProperties.LOOTR_INVENTORY;
      default -> original.call(string);
    };
  }
}
