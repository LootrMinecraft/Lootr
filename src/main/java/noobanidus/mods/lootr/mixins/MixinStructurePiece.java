package noobanidus.mods.lootr.mixins;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;
import java.util.Random;

@Mixin(StructurePiece.class)
public class MixinStructurePiece {
  @ModifyVariable(
      method = "createChest(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Ljava/util/Random;Lnet/minecraft/core/BlockPos;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/block/state/BlockState;)Z",
      at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/levelgen/structure/StructurePiece;reorient(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;")
  )
  private BlockState correctFacingGenerateChest(BlockState original, ServerLevelAccessor worldIn, BoundingBox boundsIn, Random rand, BlockPos posIn, ResourceLocation resourceLocationIn, @Nullable BlockState pState) {
    if (ConfigManager.getLootBlacklist().contains(resourceLocationIn)) {
      return original;
    }
    ResourceKey<Level> key = worldIn.getLevel().dimension();
    if (ConfigManager.isDimensionBlocked(key)) {
      return StructurePiece.reorient(worldIn, posIn, original);
    }
    return StructurePiece.reorient(worldIn, posIn, ModBlocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, original.getValue(ChestBlock.WATERLOGGED)));
  }

  @ModifyVariable(
      method = "createChest(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Ljava/util/Random;Lnet/minecraft/core/BlockPos;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/block/state/BlockState;)Z",
      at = @At("HEAD")
  )
  private BlockState replaceChest(BlockState original, ServerLevelAccessor worldIn, BoundingBox boundsIn, Random rand, BlockPos posIn, ResourceLocation resourceLocationIn, @Nullable BlockState pState) {
    if (ConfigManager.getLootBlacklist().contains(resourceLocationIn)) {
      return original;
    }
    ResourceKey<Level> key = worldIn.getLevel().dimension();
    if (ConfigManager.isDimensionBlocked(key)) {
      return original;
    }
    if (original != null) {
      BlockState replacement = ConfigManager.replacement(original);
      if (replacement != null) {
        return replacement;
      }
    }
    return original;
  }
}
