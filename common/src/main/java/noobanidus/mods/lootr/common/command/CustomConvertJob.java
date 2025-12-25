package noobanidus.mods.lootr.common.command;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrInventoryBlockEntity;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinBaseContainerBlockEntity;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// Blame ChatGPT for this travesty.
public final class CustomConvertJob {
  private static final ThreadFactory THREAD_FACTORY =
      new ThreadFactoryBuilder().setDaemon(true).setNameFormat("lootr-convert-%d").build();

  private static final TicketType<ChunkPos> CONVERT_TICKET =
      TicketType.create("lootr_convert", Comparator.comparingLong(ChunkPos::toLong));

  private static Thread convertThread;

  private static final int TICKET_LEVEL = 2;

  public static void start(MinecraftServer server, ServerLevel level, List<ChunkPos> positions, CommandSourceStack src) {
    if (convertThread != null && convertThread.isAlive()) {
      src.sendFailure(Component.literal("A conversion job is already running."));
      return;
    }

    AtomicBoolean running = new AtomicBoolean(true);
    AtomicInteger processed = new AtomicInteger();
    AtomicInteger converted = new AtomicInteger();
    AtomicInteger skipped = new AtomicInteger();
    AtomicInteger convertedBlockEntities = new AtomicInteger();

    convertThread = THREAD_FACTORY.newThread(() -> {
      try {
        final int batchSize = 2; // increase slowly; 1–4 is usually sane
        for (int i = 0; i < positions.size() && running.get(); i += batchSize) {
          int from = i;
          int to = Math.min(i + batchSize, positions.size());

          CompletableFuture<Void> batchDone = new CompletableFuture<>();

          server.execute(() -> {
            try {
              for (int j = from; j < to; j++) {
                ChunkPos cp = positions.get(j);

                int convertedCount = processOneChunkOnServerThread(level, cp, src);
                processed.incrementAndGet();
                if (convertedCount > 0) {
                  converted.incrementAndGet();
                  convertedBlockEntities.addAndGet(convertedCount);
                } else {
                  skipped.incrementAndGet();
                }
              }

              // optional progress message every N chunks (server thread safe)
              if (processed.get() % 50 == 0) {
                src.sendSuccess(() -> Component.literal(
                    "Progress: " + processed.get() + "/" + positions.size() +
                        " converted=" + converted.get() + " chunks, skipped=" + skipped.get() + " empty chunks, converted a total of " + convertedBlockEntities.get() + " block entities to custom inventories."
                ), true);
              }

              batchDone.complete(null);
            } catch (Throwable t) {
              batchDone.completeExceptionally(t);
            }
          });

          // throttle: don’t enqueue infinite work faster than the server can tick
          batchDone.join();
        }

        server.execute(() ->
            src.sendSuccess(() -> Component.literal(
                "Conversion complete. processed=" + processed.get() +
                    " converted=" + converted.get() +
                    " skipped=" + skipped.get()
            ), true)
        );
      } catch (Throwable t) {
        server.execute(() -> src.sendFailure(Component.literal("Conversion failed: " + t)));
      }
    });
    convertThread.start();
  }

  private static int processOneChunkOnServerThread(ServerLevel level, ChunkPos pos, CommandSourceStack src) {
    if (level == null) {
      src.sendFailure(Component.literal("Level not found."));
      return 0;
    }
    level.getChunkSource().addRegionTicket(CONVERT_TICKET, pos, TICKET_LEVEL, pos);
    try {
      var chunk = level.getChunk(pos.x, pos.z);

      int changed = 0;

      for (var bePos : chunk.getBlockEntitiesPos()) {
        var be = chunk.getBlockEntity(bePos, LevelChunk.EntityCreationType.IMMEDIATE);
        if (be == null) {
          continue;
        }

        if (LootrTags.BlockEntity.isTagged(be, LootrTags.BlockEntity.CUSTOM_INELIGIBLE)) {
          continue;
        }

        if (!be.getBlockState().is(LootrTags.Blocks.CUSTOM_ELIGIBLE)) {
          continue;
        }

        if (!(be instanceof BaseContainerBlockEntity container)) {
          continue;
        }

        if (container.isEmpty()) {
          continue;
        }

        // paste your conversion body here; return true if you changed something
        changed += convertAt(level, bePos, be, src);
      }

      return changed;
    } finally {
      level.getChunkSource().removeRegionTicket(CONVERT_TICKET, pos, TICKET_LEVEL, pos);
    }
  }

  private static int convertAt(ServerLevel level, BlockPos pos, BlockEntity blockEntity, CommandSourceStack src) {
    BlockState state = blockEntity.getBlockState();
    NonNullList<ItemStack> reference = ((AccessorMixinBaseContainerBlockEntity) blockEntity).invokeGetItems();
    BlockState newState = CommandLootr.updateBlockState(state, LootrRegistry.getInventoryBlock().defaultBlockState());
    NonNullList<ItemStack> custom = CommandLootr.copyItemList(reference);
    level.removeBlockEntity(pos);
    level.setBlockAndUpdate(pos, newState);
    BlockEntity te = level.getBlockEntity(pos);
    if (!(te instanceof LootrInventoryBlockEntity inventory)) {
      src.sendFailure(Component.literal("Unable to convert chest at '" + pos + "', BlockState is not a Lootr Inventory block."));
      return 0;
    } else {
      inventory.setCustomInventory(custom);
      inventory.setChanged();
      return 1;
    }
  }
}
