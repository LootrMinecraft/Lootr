package noobanidus.mods.lootr.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;

public class LootrChestMinecartEntity extends ChestMinecartEntity {
  public LootrChestMinecartEntity(EntityType<? extends ChestMinecartEntity> type, World world) {
    super(type, world);
  }

  public LootrChestMinecartEntity(World worldIn, double x, double y, double z) {
    super(worldIn, x, y, z);
  }
}
