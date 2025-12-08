package noobanidus.mods.lootr.common.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RandomizableContainer.class)
public interface MixinRandomizableContainer {
  @WrapOperation(method = "tryLoadLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/RandomizableContainer;setLootTableSeed(J)V"))
  default void lootr$tryLoadLootTable(RandomizableContainer instance, long l, Operation<Void> original) {
    original.call(instance, l);
    if (!(instance instanceof ILootrBlockEntity) && instance instanceof BlockEntity blockEntity && blockEntity.getLevel() != null && !(LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity)) {
      BlockEntityTicker.addEntity(blockEntity, blockEntity.getLevel(), new ChunkPos(blockEntity.getBlockPos()));
    }
  }

  // TODO: Maybe a hook in `unpackLootTable`? That would need to ensure that the block is convertible as we'd want to cancel unpacking in the event that the block is converting, but otherwise continue as normal.
}
