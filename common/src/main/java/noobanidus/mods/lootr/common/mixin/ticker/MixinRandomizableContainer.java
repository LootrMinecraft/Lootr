package noobanidus.mods.lootr.common.mixin.ticker;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
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
    if (/*instance.getLootTable() != null && */!(instance instanceof ILootrBlockEntity) && instance instanceof BlockEntity blockEntity && blockEntity.getLevel() != null && !(LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity)) {
      BlockEntityTicker.addEntity(blockEntity, blockEntity.getLevel(), new ChunkPos(blockEntity.getBlockPos()));
    }
  }

  @WrapOperation(method="setLootTable(Lnet/minecraft/resources/ResourceKey;J)V", at=@At(value="INVOKE", target="Lnet/minecraft/world/RandomizableContainer;setLootTable(Lnet/minecraft/resources/ResourceKey;)V"))
  default void lootr$setLootTable(RandomizableContainer instance, ResourceKey<LootTable> table, Operation<Void> original) {
    original.call(instance, table);
    if (table != null && !(instance instanceof ILootrBlockEntity) && instance instanceof BlockEntity blockEntity && blockEntity.getLevel() != null && !(LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity)) {
      BlockEntityTicker.addEntity(blockEntity, blockEntity.getLevel(), new ChunkPos(blockEntity.getBlockPos()));
    }
  }

  @WrapMethod(method="unpackLootTable")
  default void lootr$unpackLootTable(Player player, Operation<Void> original) {
    if (this instanceof BlockEntity blockEntity) {
      // TODO: Configuration for this
      if (BlockEntityTicker.isValidEntityFull(blockEntity)) {
        BlockEntityTicker.addEntity(blockEntity, blockEntity.getLevel(), new ChunkPos(blockEntity.getBlockPos()));
        return;
      }
    }

    original.call(player);
  }
}
