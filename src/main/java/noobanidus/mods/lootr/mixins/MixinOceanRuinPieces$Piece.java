package noobanidus.mods.lootr.mixins;

import net.minecraft.block.ChestBlock;
import net.minecraft.loot.LootTables;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.OceanRuinPieces;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(OceanRuinPieces.Piece.class)
public class MixinOceanRuinPieces$Piece {
  @Inject(method = "Lnet/minecraft/world/gen/feature/structure/OceanRuinPieces$Piece;handleDataMarker(Ljava/lang/String;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IServerWorld;Ljava/util/Random;Lnet/minecraft/util/math/MutableBoundingBox;)V",
      at = @At(value = "HEAD"),
      cancellable = true)
  protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb, CallbackInfo info) {
    if ("chest".equals(function)) {
      boolean large = ((OceanRuinPieces.Piece) (Object) this).isLarge;
      if (ConfigManager.getLootBlacklist().contains(large ? LootTables.CHESTS_UNDERWATER_RUIN_BIG : LootTables.CHESTS_UNDERWATER_RUIN_SMALL)) {
        return;
      }
      RegistryKey<World> key = worldIn.getWorld().getDimensionKey();
      if (!ConfigManager.getDimensionWhitelist().contains(key) || ConfigManager.getDimensionBlacklist().contains(key)) {
        return;
      }
      worldIn.setBlockState(pos, ModBlocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, worldIn.getFluidState(pos).isTagged(FluidTags.WATER)), 2);
      LockableLootTileEntity.setLootTable(worldIn, rand, pos, large ? LootTables.CHESTS_UNDERWATER_RUIN_BIG : LootTables.CHESTS_UNDERWATER_RUIN_SMALL);
    }
  }
}