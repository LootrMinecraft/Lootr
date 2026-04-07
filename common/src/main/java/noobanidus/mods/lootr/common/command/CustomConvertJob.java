package noobanidus.mods.lootr.common.command;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

// Blame ChatGPT for this travesty, although it's not so bad.
public final class CustomConvertJob {
  private static final ThreadFactory THREAD_FACTORY =
      new ThreadFactoryBuilder().setDaemon(true).setNameFormat("lootr-convert-%d").build();

  private static Thread convertThread;

  private static final int TICKET_LEVEL = 2;

  private static final int batchSize = 2;

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
    level.getChunkSource().addTicketWithRadius(TicketType.FORCED, pos, 0);
    Consumer<String> reporter = msg -> src.sendSuccess(() -> Component.literal(msg), true);
    try {
      var chunk = level.getChunk(pos.x(), pos.z());

      int changed = 0;

      for (var bePos : chunk.getBlockEntitiesPos()) {
        var be = chunk.getBlockEntity(bePos, LevelChunk.EntityCreationType.IMMEDIATE);
        if (be == null) {
          continue;
        }

        changed += convertAt(bePos, level, reporter, src.registryAccess());
      }

      return changed;
    } finally {
      level.getChunkSource().removeTicketWithRadius(TicketType.FORCED, pos, 0);
    }
  }

  private static int convertAt(BlockPos pos, ServerLevel level, Consumer<String> reporter, HolderLookup.Provider registries) {
    if (CommandLootr.convertToCustom(pos, level, reporter, registries)) {
      return 1;
    }

    return 0;
  }
}
