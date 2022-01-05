package noobanidus.mods.lootr.mixins;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import noobanidus.mods.lootr.api.blockentity.ILootTile;
import noobanidus.mods.lootr.blocks.entities.TileTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(Level.class)
public class MixinLevel {
  @Inject(method="addFreshBlockEntities", at=@At(value="RETURN"))
  private void lootrAddFreshBlockEntities (Collection<BlockEntity> entities, CallbackInfo cir) {
    Level level = (Level) (Object) this;
    if (!level.isClientSide()) {
      for (BlockEntity be : entities) {
        if (be instanceof RandomizableContainerBlockEntity && !(be instanceof ILootTile)) {
          TileTicker.addEntry(level.dimension(), be.getBlockPos());
        }
      }
    }
  }
}
