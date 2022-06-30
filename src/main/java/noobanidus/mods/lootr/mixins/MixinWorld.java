package noobanidus.mods.lootr.mixins;

import net.minecraft.tileentity.TileEntityLockableLoot;
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
  @Inject(method = "addTileEntity", at = @At(target = "Lnet/minecraft/tileentity/TileEntity;onLoad()V", value = "INVOKE", remap = false, shift = At.Shift.AFTER))
  protected void lootrAddBlockEntity(TileEntity tile, CallbackInfoReturnable<Boolean> cir) {
    if (!(tile instanceof TileEntityLockableLoot) || tile instanceof ILootTile) {
      return;
    }

    TileTicker.addEntry(((World) (Object) this).provider.getDimension(), tile.getPos());
  }
}
