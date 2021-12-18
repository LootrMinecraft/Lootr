package noobanidus.mods.lootr.mixins;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.IglooPieces;
import noobanidus.mods.lootr.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(IglooPieces.IglooPiece.class)
public class MixinIglooPieces$IglooPiece {
  @Inject(method = "handleDataMarker",
      at = @At(value = "HEAD"),
      cancellable = true)
  protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor worldIn, Random rand, BoundingBox sbb, CallbackInfo info) {
    if (ConfigManager.getLootBlacklist().contains(BuiltInLootTables.IGLOO_CHEST)) {
      return;
    }
    if ("chest".equals(function)) {
      ResourceKey<Level> key = worldIn.getLevel().dimension();
      if (ConfigManager.isDimensionBlocked(key)) {
        return;
      }
      worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
      RandomizableContainerBlockEntity.setLootTable(worldIn, rand, pos.below(), BuiltInLootTables.IGLOO_CHEST);
      info.cancel();
    }
  }
}
