package noobanidus.mods.lootr.mixins;

import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.IglooPieces;
import noobanidus.mods.lootr.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(IglooPieces.Piece.class)
public class MixinIglooPieces$Piece {
  @Inject(method = "handleDataMarker",
      at = @At(value = "HEAD"),
      cancellable = true)
  protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb, CallbackInfo info) {
    if (ConfigManager.getLootBlacklist().contains(LootTables.IGLOO_CHEST)) {
      return;
    }
    if ("chest".equals(function)) {
      RegistryKey<World> key = worldIn.getLevel().dimension();
      if (ConfigManager.isDimensionBlocked(key)) {
        return;
      }
      worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
      LockableLootTileEntity.setLootTable(worldIn, rand, pos.below(), LootTables.IGLOO_CHEST);
      info.cancel();
    }
  }
}
