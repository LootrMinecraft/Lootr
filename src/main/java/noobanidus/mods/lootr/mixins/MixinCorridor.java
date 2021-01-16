package noobanidus.mods.lootr.mixins;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.MineshaftPieces;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(MineshaftPieces.Corridor.class)
public abstract class MixinCorridor extends StructurePiece {
  protected MixinCorridor(IStructurePieceType structurePieceTypeIn, int componentTypeIn) {
    super(structurePieceTypeIn, componentTypeIn);
  }

  @Inject(
      method = "Lnet/minecraft/world/gen/feature/structure/MineshaftPieces$Corridor;generateChest(Lnet/minecraft/world/ISeedReader;Lnet/minecraft/util/math/MutableBoundingBox;Ljava/util/Random;IIILnet/minecraft/util/ResourceLocation;)Z",
      at = @At(value = "HEAD"),
      cancellable = true
  )
  private void generateChest(ISeedReader worldIn, MutableBoundingBox structurebb, Random randomIn, int x, int y, int z, ResourceLocation loot, CallbackInfoReturnable<Boolean> info) {
    BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
    info.setReturnValue(this.generateChest(worldIn, structurebb, randomIn, blockpos, loot, null));
    info.cancel();
  }
}
