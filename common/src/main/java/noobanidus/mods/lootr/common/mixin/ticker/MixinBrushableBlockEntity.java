package noobanidus.mods.lootr.common.mixin.ticker;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BrushableBlockEntity.class)
public class MixinBrushableBlockEntity {
  // Confer MixinRandomizableContainer
  // Brushables are not a container
  @WrapOperation(method = "tryLoadLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;contains(Ljava/lang/String;I)Z"))
  public boolean lootr$tryLoadLootTable(CompoundTag instance, String string, int i, Operation<Boolean> original) {
    if (original.call(instance, string, i)) {
      BrushableBlockEntity bbe = (BrushableBlockEntity) (Object) this;
      if (bbe.getLevel() != null) {
        BlockEntityTicker.addEntity(bbe, bbe.getLevel(), new ChunkPos(bbe.getBlockPos()));
      }
      return true;
    }
    return false;
  }
}
