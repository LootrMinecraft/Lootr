package noobanidus.mods.lootr.mixins;

import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import noobanidus.mods.lootr.data.BooleanData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(value = LockableLootTileEntity.class)
public abstract class MixinLockableLootTileEntity {
  @Overwrite
  public static void setLootTable(IBlockReader reader, Random rand, BlockPos pos, ResourceLocation lootTableIn) {
    TileEntity tileentity = reader.getTileEntity(pos);
    if (tileentity instanceof LockableLootTileEntity) {
      ((LockableLootTileEntity) tileentity).setLootTable(lootTableIn, rand.nextLong());
      if (reader instanceof IWorld) {
        IWorld world = (IWorld) reader;
        BooleanData.markLootChest(world, pos);
      }
    }
  }
}
