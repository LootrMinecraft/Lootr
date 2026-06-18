package noobanidus.mods.lootr.common.impl.command.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.api.interfaces.command.ILootrCommandEntityExtension;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoService(ILootrCommandEntityExtension.class)
public class ItemFrameCommandType implements ILootrCommandEntityExtension<LootrItemFrame> {
  @Override
  public String getId() {
    return "item_frame";
  }

  @Override
  public EntityType<LootrItemFrame> getType() {
    //noinspection unchecked
    return (EntityType<LootrItemFrame>) LootrRegistry.getItemFrame();
  }

  @Override
  public LootrItemFrame createEntity(Level level, BlockPos pos) {
    return new LootrItemFrame(level, pos, Direction.UP);
  }

  @Override
  public void processInternal(LootrItemFrame entity, @Nullable Entity creator, @NotNull ResourceKey<LootTable> lootTable, long seed) {
    ItemStack item = new ItemStack(BuiltInRegistries.ITEM.getRandom(entity.getRandom()).map(Holder::value)
        .orElse(Items.CHEST));
    entity.lootrSetItem(item);
  }
}
