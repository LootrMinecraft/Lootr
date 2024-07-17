package noobanidus.mods.lootr.common.api.registry;

import net.minecraft.stats.Stat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import noobanidus.mods.lootr.common.api.advancement.IAdvancementTrigger;
import noobanidus.mods.lootr.common.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.advancement.ILootedStatTrigger;

public class LootrRegistry {
  public static ILootrRegistry INSTANCE;

  public static Block getBarrelBlock() {
    return INSTANCE.getBarrelBlock();
  }

  public static Block getChestBlock() {
    return INSTANCE.getChestBlock();
  }

  public static Block getTrappedChestBlock() {
    return INSTANCE.getTrappedChestBlock();
  }

  public static Block getInventoryBlock() {
    return INSTANCE.getInventoryBlock();
  }

  public static Block getTrophyBlock() {
    return INSTANCE.getTrophyBlock();
  }

  public static Block getShulkerBlock() {
    return INSTANCE.getShulker();
  }

  public static Item getBarrelItem() {
    return INSTANCE.getBarrelItem();
  }

  public static Item getChestItem() {
    return INSTANCE.getChestItem();
  }

  public static Item getTrappedChestItem() {
    return INSTANCE.getTrappedChestItem();
  }

  public static Item getInventoryItem() {
    return INSTANCE.getInventoryItem();
  }

  public static Item getTrophyItem() {
    return INSTANCE.getTrophyItem();
  }

  public static Item getShulkerItem() {
    return INSTANCE.getShulkerItem();
  }

  public static EntityType<?> getMinecart() {
    return INSTANCE.getMinecart();
  }

  public static BlockEntityType<?> getBarrelBlockEntity() {
    return INSTANCE.getBarrelBlockEntity();
  }

  public static BlockEntityType<? extends ChestBlockEntity> getChestBlockEntity() {
    return INSTANCE.getChestBlockEntity();
  }

  public static BlockEntityType<? extends ChestBlockEntity> getTrappedChestBlockEntity() {
    return INSTANCE.getTrappedChestBlockEntity();
  }

  public static BlockEntityType<? extends ChestBlockEntity> getInventoryBlockEntity() {
    return INSTANCE.getInventoryBlockEntity();
  }

  public static BlockEntityType<?> getShulkerBlockEntity() {
    return INSTANCE.getShulkerBlockEntity();
  }

  public static LootItemConditionType getLootCount() {
    return INSTANCE.getLootCount();
  }

  public static IAdvancementTrigger getAdvancementTrigger() {
    return INSTANCE.getAdvancementTrigger();
  }

  public static IContainerTrigger getChestTrigger() {
    return INSTANCE.getChestTrigger();
  }

  public static IContainerTrigger getBarrelTrigger() {
    return INSTANCE.getBarrelTrigger();
  }

  public static IContainerTrigger getCartTrigger() {
    return INSTANCE.getCartTrigger();
  }

  public static IContainerTrigger getShulkerTrigger() {
    return INSTANCE.getShulkerTrigger();
  }

  public static ILootedStatTrigger getStatTrigger() {
    return INSTANCE.getStatTrigger();
  }

  public static Stat<?> getLootedStat() {
    return INSTANCE.getLootedStat();
  }

  public static CreativeModeTab getTab() {
    return INSTANCE.getTab();
  }
}
