package noobanidus.mods.lootr.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.EndCityPieces;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(EndCityPieces.EndCityPiece.class)
public class MixinEndCityPieces$EndCityPiece {
  @Inject(method = "handleDataMarker", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevelAccessor;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
  private void LootrHandleDataMarker(String marker, BlockPos position, ServerLevelAccessor level, Random random, BoundingBox boundingBox, CallbackInfo ci) {
    if (!ConfigManager.CONVERT_ELYTRAS.get()) {
      return;
    }
    if (marker.startsWith("Elytra")) {
      EndCityPieces.EndCityPiece piece = (EndCityPieces.EndCityPiece) (Object) this;
      level.setBlock(position.below(), Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, piece.getRotation().rotate(Direction.SOUTH)), 3);
      if (level.getBlockEntity(position.below()) instanceof RandomizableContainerBlockEntity chest) {
        chest.setLootTable(LootrAPI.ELYTRA_CHEST, random.nextLong());
      }
      ci.cancel();
    }
  }
}
