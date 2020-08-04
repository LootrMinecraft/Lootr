package noobanidus.mods.lootr.events;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.ILootTile;

public class HandleMinecart {
  public static void onEntityJoin(EntityJoinWorldEvent event) {
    if (event.getEntity().getType() == EntityType.CHEST_MINECART) {
      ContainerMinecartEntity entity = (ContainerMinecartEntity) event.getEntity();
      if (entity.lootTable != null) {
        if (ConfigManager.CONVERT_MINESHAFTS.get()) {
          BlockPos pos = new BlockPos(entity.getPosX(), entity.getPosY(), entity.getPosZ());
          World world = entity.world;
          ResourceLocation lootTable = entity.lootTable;
          long seed = entity.lootTableSeed;
          event.setCanceled(true);
          entity.remove();
          if (!world.isRemote) {
            world.setBlockState(pos, ModBlocks.CHEST.getDefaultState());
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof ILootTile) {
              ((ILootTile) te).setTable(lootTable);
              ((ILootTile) te).setSeed(seed);
            }
          }
        }
      }
    }
  }
}
