package noobanidus.mods.lootr.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.StructurePiece;
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
      method = "createChest(Lnet/minecraft/world/IServerWorld;Lnet/minecraft/util/math/MutableBoundingBox;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/block/BlockState;)Z",
      at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/gen/feature/structure/StructurePiece;reorient(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockState;")
  )
  private BlockState correctFacingGenerateChest(BlockState original, IServerWorld worldIn, MutableBoundingBox boundsIn, Random rand, BlockPos posIn, ResourceLocation resourceLocationIn, @Nullable BlockState pState) {
    if (ConfigManager.getLootBlacklist().contains(resourceLocationIn)) {
      return original;
    }
    RegistryKey<World> key = worldIn.getLevel().dimension();
    if (ConfigManager.isDimensionBlocked(key)) {
      return StructurePiece.reorient(worldIn, posIn, original);
    }
    return StructurePiece.reorient(worldIn, posIn, ModBlocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, original.getValue(ChestBlock.WATERLOGGED)));
  }

  @ModifyVariable(
      method = "createChest(Lnet/minecraft/world/IServerWorld;Lnet/minecraft/util/math/MutableBoundingBox;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/block/BlockState;)Z",
      at = @At("HEAD")
  )
  private BlockState replaceChest(BlockState original, IServerWorld worldIn, MutableBoundingBox boundsIn, Random rand, BlockPos posIn, ResourceLocation resourceLocationIn, @Nullable BlockState pState) {
    if (ConfigManager.getLootBlacklist().contains(resourceLocationIn)) {
      return original;
    }
    RegistryKey<World> key = worldIn.getLevel().dimension();
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
