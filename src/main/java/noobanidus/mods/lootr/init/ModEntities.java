package noobanidus.mods.lootr.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

public class ModEntities {
  public static EntityType<LootrChestMinecartEntity> LOOTR_MINECART_ENTITY;

  public static void register() {
    LOOTR_MINECART_ENTITY = Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(LootrAPI.MODID, "lootr_minecart"), FabricEntityTypeBuilder.create(MobCategory.MISC, new EntityType.EntityFactory<LootrChestMinecartEntity>() {
      @Override
      public LootrChestMinecartEntity create(EntityType<LootrChestMinecartEntity> entityType, Level level) {
        return new LootrChestMinecartEntity(entityType, level);
      }
    }).dimensions(EntityDimensions.fixed(0.9f, 1.4f)).trackRangeBlocks(8).build());
  }
}
