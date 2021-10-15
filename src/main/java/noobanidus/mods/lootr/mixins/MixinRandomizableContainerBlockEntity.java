package noobanidus.mods.lootr.mixins;

import com.google.common.collect.Sets;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.ILootTile;
import noobanidus.mods.lootr.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;
import java.util.Set;

@Mixin(RandomizableContainerBlockEntity.class)
public class MixinRandomizableContainerBlockEntity {
  private final Logger log = LogManager.getLogger(Lootr.MODID);

  @Inject(method = "setLootTable(Lnet/minecraft/world/level/BlockGetter;Ljava/util/Random;Lnet/minecraft/core/BlockPos;Lnet/minecraft/resources/ResourceLocation;)V",
      at = @At("HEAD"))
  private static void setLootTable(BlockGetter reader, Random rand, BlockPos pos, ResourceLocation lootTableIn, CallbackInfo info) {
    if (ConfigManager.getLootBlacklist().contains(lootTableIn)) {
      return;
    }
    if (reader instanceof ServerLevelAccessor) {
      BlockState state = reader.getBlockState(pos);
      BlockState replacement = ConfigManager.replacement(state);
      if (replacement != null) {
        ServerLevelAccessor world = (ServerLevelAccessor) reader;
        ResourceKey<Level> key = world.getLevel().dimension();
        if (ConfigManager.isDimensionBlocked(key)) {
          return;
        }
        ChunkAccess chunk = world.getChunk(pos);
        chunk.removeBlockEntity(pos);
        if (state.getProperties().contains(ChestBlock.WATERLOGGED)) {
          replacement = replacement.setValue(ChestBlock.WATERLOGGED, state.getValue(ChestBlock.WATERLOGGED));
        }
        world.setBlock(pos, replacement, 2);
        BlockEntity te = ((EntityBlock)replacement.getBlock()).newBlockEntity(pos, replacement);
        if (te != null) {
          chunk.setBlockEntity(te);
        }
      }
    }
  }

  private static final ResourceLocation REPURPOSED_END_SHULKER = new ResourceLocation("repurposed_structures", "chests/dungeon/end");

  @Inject(method = "setLootTable(Lnet/minecraft/resources/ResourceLocation;J)V", at = @At("HEAD"))
  private void setLootTable(ResourceLocation table, long seed, CallbackInfo info) {
    if (this instanceof ILootTile || !ConfigManager.REPORT_TABLES.get()) {
      return;
    }

    Set<String> modids = Sets.newHashSet("apotheosis", "artifacts");
    if (table != null && modids.contains(table.getNamespace()) || REPURPOSED_END_SHULKER.equals(table)) {
      return;
    }


    StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();

    log.error("\n=================================================" +
        "\n  Lootr detected a loot chest creation that it " +
        "\n  can't replace. Please consider reporting it!" +
        "\n    Tile: " + this +
        "\n    Table: " + table +
        "\n    Location: " + ((RandomizableContainerBlockEntity) (Object) (this)).getBlockPos().toString() +
        "\n    Stack: " + stacktrace[3].toString() +
        "\n           " + stacktrace[4].toString() +
        "\n           " + stacktrace[5].toString());
  }
}
