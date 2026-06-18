package noobanidus.mods.lootr.common.api.interfaces.command;

import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILootrCommandEntityExtension<T extends Entity> extends ILootrCommandExtension {
  EntityType<T> getType();
  default T createEntity (Level level, BlockPos pos) {
    T result = getType().create(level, EntitySpawnReason.COMMAND);
    if (result != null) {
      result.setPos(Vec3.atCenterOf(pos));
    }
    return result;
  }
  default void process (Entity entity, @Nullable Entity creator, @NotNull ResourceKey<LootTable> lootTable, long seed) {
    //noinspection unchecked
    processInternal((T) entity, creator, lootTable, seed);
  }

  default void processInternal (T entity, @Nullable Entity creator, @NotNull ResourceKey<LootTable> lootTable, long seed) {
    // NO-OP
  }
}
