package noobanidus.mods.lootr.mixins;

import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.OceanRuinPieces;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(OceanRuinPieces.OceanRuinPiece.class)
public class MixinOceanRuinPieces$Piece {
  @Inject(method = "handleDataMarker",
      at = @At(value = "HEAD"),
      cancellable = true)
  protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor worldIn, Random rand, BoundingBox sbb, CallbackInfo info) {
    if ("chest".equals(function)) {
      boolean large = ((OceanRuinPieces.OceanRuinPiece) (Object) this).isLarge;
      if (ConfigManager.getLootBlacklist().contains(large ? BuiltInLootTables.UNDERWATER_RUIN_BIG : BuiltInLootTables.UNDERWATER_RUIN_SMALL)) {
        return;
      }
      ResourceKey<Level> key = worldIn.getLevel().dimension();
      if (ConfigManager.isDimensionBlocked(key)) {
        return;
      }
      worldIn.setBlock(pos, ModBlocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, worldIn.getFluidState(pos).is(FluidTags.WATER)), 2);
      RandomizableContainerBlockEntity.setLootTable(worldIn, rand, pos, large ? BuiltInLootTables.UNDERWATER_RUIN_BIG : BuiltInLootTables.UNDERWATER_RUIN_SMALL);
      info.cancel();
    }
  }
}