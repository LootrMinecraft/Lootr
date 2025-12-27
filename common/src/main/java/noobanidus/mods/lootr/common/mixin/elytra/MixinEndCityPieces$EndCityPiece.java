package noobanidus.mods.lootr.common.mixin.elytra;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCityPieces.EndCityPiece.class)
public class MixinEndCityPieces$EndCityPiece {
  @Inject(method = "handleDataMarker", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevelAccessor;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true, require = 0)
  private void LootrHandleDataMarker(String marker, BlockPos position, ServerLevelAccessor level, RandomSource random, BoundingBox boundingBox, CallbackInfo ci) {
    if (LootrAPI.isDisabled()) {
      return;
    }
    if (LootrAPI.shouldConvertElytrasToItemFrames()) {
      if (marker.startsWith("Elytra")) {
        LootrItemFrame itemframe = new LootrItemFrame(level.getLevel(), position, ((EndCityPieces.EndCityPiece) (Object) this).placeSettings()
            .getRotation().rotate(Direction.SOUTH));
        itemframe.lootrSetItem(new ItemStack(Items.ELYTRA));
        level.addFreshEntity(itemframe);
        ci.cancel();
      }
    } else if (LootrAPI.shouldConvertElytrasToChests()) {
      if (marker.startsWith("Elytra")) {
        EndCityPieces.EndCityPiece piece = (EndCityPieces.EndCityPiece) (Object) this;
        // We use `Blocks.CHEST` so that we can obey the disabled configuration and
        // rely on the block ticker to convert. Definitely not because of laziness.
        level.setBlock(position.below(), Blocks.CHEST.defaultBlockState()
            .setValue(ChestBlock.FACING, piece.getRotation().rotate(Direction.SOUTH)), 3);
        if (level.getBlockEntity(position.below()) instanceof RandomizableContainerBlockEntity chest) {
          chest.setLootTable(LootrAPI.ELYTRA_CHEST, random.nextLong());
        }
        ci.cancel();
      }
    }
  }
}
