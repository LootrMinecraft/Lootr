package noobanidus.mods.lootr.api.registry;

import net.minecraft.stats.Stat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import noobanidus.mods.lootr.api.advancement.IAdvancementTrigger;
import noobanidus.mods.lootr.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.api.advancement.ILootedStatTrigger;

public interface ILootrRegistry {
  Block getBarrel();

  Block getChest();

  Block getTrappedChest();

  Block getInventory();

  Block getTrophy();

  Block getShulker();

  Item getBarrelItem();

  Item getChestItem();

  Item getTrappedChestItem();

  Item getInventoryItem();

  Item getTrophyItem();

  Item getShulkerItem();

  EntityType<?> getMinecart();

  BlockEntityType<?> getBarrelBlockEntity();

  BlockEntityType<?> getChestBlockEntity();

  BlockEntityType<?> getTrappedChestBlockEntity();

  BlockEntityType<?> getInventoryBlockEntity();

  BlockEntityType<?> getShulkerBlockEntity();

  // TODO: Hm
  IAdvancementTrigger getAdvancementTrigger();

  IContainerTrigger getChestTrigger();

  IContainerTrigger getBarrelTrigger();

  IContainerTrigger getCartTrigger();

  IContainerTrigger getShulkerTrigger();

  ILootedStatTrigger getStatTrigger();

  LootItemConditionType getLootCount();

  Stat<?> getLootedStat();

  CreativeModeTab getTab();
}
