package noobanidus.mods.lootr.common.impl.command.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.api.interfaces.command.ILootrCommandEntityExtension;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrCommandEntityExtension.class)
public class MinecartCommandType implements ILootrCommandEntityExtension<LootrChestMinecartEntity> {
  @Override
  public EntityType<LootrChestMinecartEntity> getType() {
    //noinspection unchecked
    return (EntityType<LootrChestMinecartEntity>) LootrRegistry.getMinecart();
  }

  @Override
  public LootrChestMinecartEntity createEntity(Level level, BlockPos pos) {
    return new LootrChestMinecartEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
  }

  @Override
  public void processInternal(LootrChestMinecartEntity entity, @Nullable Entity creator, @NotNull ResourceKey<LootTable> lootTable, long seed) {
    if (creator != null) {
      entity.setYRot(creator.getYRot());
    }

    entity.setLootTableInternal(lootTable, seed);
  }

  @Override
  public String getId() {
    return "cart";
  }
}
