package noobanidus.mods.lootr.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.tiles.SpecialLootBarrelTile;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;
import noobanidus.mods.lootr.tiles.SpecialTrappedLootChestTile;

public class ModEntities {
  public static EntityType<LootrChestMinecartEntity> LOOTR_MINECART_ENTITY = EntityType.Builder.<LootrChestMinecartEntity>of(LootrChestMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8).setCustomClientFactory((entity, world) -> new LootrChestMinecartEntity(ModEntities.LOOTR_MINECART_ENTITY, world)).build("lootr_minecart");

  static {
    LOOTR_MINECART_ENTITY.setRegistryName(Lootr.MODID, "lootr_minecart");
  }

  public static void registerEntityType(RegistryEvent.Register<EntityType<?>> event) {
    event.getRegistry().register(LOOTR_MINECART_ENTITY);
  }
}
