package noobanidus.mods.lootr.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.block.tile.*;

@Mod.EventBusSubscriber(modid=Lootr.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ModTiles {
  public static TileEntityType<LootrChestTileEntity> LOOT_CHEST = TileEntityType.Builder.of(LootrChestTileEntity::new, ModBlocks.CHEST).build(null);
  public static TileEntityType<TrappedLootrChestTileEntity> TRAPPED_LOOT_CHEST = TileEntityType.Builder.of(TrappedLootrChestTileEntity::new, ModBlocks.TRAPPED_CHEST).build(null);
  public static TileEntityType<LootrBarrelTileEntity> LOOT_BARREL = TileEntityType.Builder.of(LootrBarrelTileEntity::new, ModBlocks.BARREL).build(null);
  public static TileEntityType<LootrInventoryTileEntity> LOOT_INVENTORY = TileEntityType.Builder.of(LootrInventoryTileEntity::new, ModBlocks.INVENTORY).build(null);
  public static TileEntityType<LootrShulkerTileEntity> LOOK_SHULKER = TileEntityType.Builder.of(LootrShulkerTileEntity::new, ModBlocks.SHULKER).build(null);

  @SubscribeEvent
  public static void registerTileEntityType(RegistryEvent.Register<TileEntityType<?>> event) {
    LOOT_CHEST.setRegistryName(Lootr.MODID, "special_loot_chest");
    event.getRegistry().register(LOOT_CHEST);
    TRAPPED_LOOT_CHEST.setRegistryName(Lootr.MODID, "special_trapped_loot_chest");
    event.getRegistry().register(TRAPPED_LOOT_CHEST);
    LOOT_BARREL.setRegistryName(Lootr.MODID, "special_loot_barrel");
    event.getRegistry().register(LOOT_BARREL);
    LOOT_INVENTORY.setRegistryName(Lootr.MODID, "special_loot_inventory");
    event.getRegistry().register(LOOT_INVENTORY);
    LOOK_SHULKER.setRegistryName(Lootr.MODID, "special_loot_shulker");
    event.getRegistry().register(LOOK_SHULKER);
  }
}
