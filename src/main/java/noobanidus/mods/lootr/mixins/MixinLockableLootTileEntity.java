package noobanidus.mods.lootr.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IServerWorld;
import noobanidus.mods.lootr.init.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(LockableLootTileEntity.class)
public class MixinLockableLootTileEntity {
   @Inject(method="Lnet/minecraft/tileentity/LockableLootTileEntity;setLootTable(Lnet/minecraft/world/IBlockReader;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/ResourceLocation;)V",
   at = @At("HEAD"))
   private static void setLootTable(IBlockReader reader, Random rand, BlockPos pos, ResourceLocation lootTableIn, CallbackInfo info) {
      if (reader instanceof IServerWorld) {
         BlockState state = reader.getBlockState(pos);
         ((IServerWorld) reader).setBlockState(pos, ModBlocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, state.get(ChestBlock.WATERLOGGED)).with(ChestBlock.FACING, state.get(ChestBlock.FACING)), 2);
      }
   }
}
