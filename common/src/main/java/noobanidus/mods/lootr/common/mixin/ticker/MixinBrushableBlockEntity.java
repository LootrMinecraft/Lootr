package noobanidus.mods.lootr.common.mixin.ticker;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrushableBlockEntity.class)
public class MixinBrushableBlockEntity {
  @Shadow
  private ResourceKey<LootTable> lootTable;
  @Shadow
  private long lootTableSeed;

  @Inject(method = "tryLoadLootTable", at = @At(value = "RETURN"), cancellable = true)
  public void lootr$tryLoadLootTable(ValueInput input, CallbackInfoReturnable<Boolean> cir) {
    BrushableBlockEntity instance = (BrushableBlockEntity) (Object) this;
    if (instance.getLevel() != null && lootTable != null) {
      BlockEntityTicker.addEntity(instance, instance.getLevel(), new ChunkPos(instance.getBlockPos()));
      cir.setReturnValue(true);
    }
  }
}
