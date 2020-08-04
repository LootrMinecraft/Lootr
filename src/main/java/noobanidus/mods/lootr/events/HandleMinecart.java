package noobanidus.mods.lootr.events;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.util.TickManager;

public class HandleMinecart {
  public static void onEntityJoin(EntityJoinWorldEvent event) {
    if (event.getEntity().getType() == EntityType.CHEST_MINECART) {
      ContainerMinecartEntity entity = (ContainerMinecartEntity) event.getEntity();
      if (entity.lootTable != null) {
        if (ConfigManager.CONVERT_MINESHAFTS.get()) {
          BlockPos pos = new BlockPos(entity.getPosX(), entity.getPosY(), entity.getPosZ());
          DimensionType type = entity.world.getDimension().getType();
          World world = entity.world;
          ResourceLocation lootTable = entity.lootTable;
          long seed = entity.lootTableSeed;
          if (!world.isRemote) {
            TickManager.addTicker(entity, seed, lootTable, pos, type);
          }
        }
      }
    }
  }
}
