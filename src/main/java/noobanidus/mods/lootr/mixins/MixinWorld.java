package noobanidus.mods.lootr.mixins;

import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.block.tile.TileTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {
  @Inject(method = "addBlockEntity", at = @At(target = "Lnet/minecraft/tileentity/TileEntity;onLoad()V", value = "INVOKE", shift = At.Shift.AFTER))
  protected void lootrAddBlockEntity(TileEntity tile, CallbackInfoReturnable<Boolean> cir) {
    World world = (World) (Object) this;
    if (world.isClientSide()) {
      return;
    }

    if (!(tile instanceof LockableLootTileEntity) || tile instanceof ILootTile) {
      return;
    }

    TileTicker.addEntry(world, tile.getBlockPos());
  }
}
