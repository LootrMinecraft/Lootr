package noobanidus.mods.lootr.common.mixin.ticker;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomizableContainer.class)
public interface MixinRandomizableContainer {
  @Shadow
  ResourceKey<LootTable> getLootTable();

  @Shadow
  Level getLevel();

  @WrapOperation(method = "tryLoadLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/RandomizableContainer;setLootTableSeed(J)V"))
  default void lootr$tryLoadLootTable(RandomizableContainer instance, long l, Operation<Void> original) {
    original.call(instance, l);
    if (instance.getLevel() != null && !instance.getLevel()
        .isClientSide() && instance instanceof BlockEntity blockEntity && !(instance instanceof ILootrBlockEntity) && !(LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity)) {
      BlockEntityTicker.addEntity(blockEntity, blockEntity.getLevel(), new ChunkPos(blockEntity.getBlockPos()));
    }
  }

  @WrapOperation(method = "setLootTable(Lnet/minecraft/resources/ResourceKey;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/RandomizableContainer;setLootTable(Lnet/minecraft/resources/ResourceKey;)V"))
  default void lootr$setLootTable(RandomizableContainer instance, ResourceKey<LootTable> table, Operation<Void> original) {
    original.call(instance, table);
    if (table != null && instance.getLevel() != null && !instance.getLevel()
        .isClientSide() && instance instanceof BlockEntity blockEntity && !(instance instanceof ILootrBlockEntity) && !(LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity)) {
      BlockEntityTicker.addEntity(blockEntity, blockEntity.getLevel(), new ChunkPos(blockEntity.getBlockPos()));
    }
  }

  // Can't be WrapMethod 'cos it's an interface
  @Inject(method = "unpackLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"), cancellable = true)
  default void lootr$unpackLootTable(Player player, CallbackInfo ci) {
    if (this instanceof BlockEntity blockEntity) {
      if (BlockEntityTicker.isValidEntityFull(blockEntity)) {
        BlockEntityTicker.addEntity(blockEntity, blockEntity.getLevel(), new ChunkPos(blockEntity.getBlockPos()));
        ci.cancel();
      }
    }
  }
}
