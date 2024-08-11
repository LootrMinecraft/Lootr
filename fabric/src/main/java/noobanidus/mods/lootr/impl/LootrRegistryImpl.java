package noobanidus.mods.lootr.impl;

import net.minecraft.stats.Stat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import noobanidus.mods.lootr.api.advancement.IAdvancementTrigger;
import noobanidus.mods.lootr.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.api.advancement.ILootedStatTrigger;
import noobanidus.mods.lootr.api.registry.ILootrRegistry;
import noobanidus.mods.lootr.init.*;

public class LootrRegistryImpl implements ILootrRegistry {
  @Override
  public Block getBarrelBlock() {
    return ModBlocks.BARREL;
  }

  @Override
  public Block getChestBlock() {
    return ModBlocks.CHEST;
  }

  @Override
  public Block getTrappedChestBlock() {
    return ModBlocks.TRAPPED_CHEST;
  }

  @Override
  public Block getInventoryBlock() {
    return ModBlocks.INVENTORY;
  }

  @Override
  public Block getTrophyBlock() {
    return ModBlocks.TROPHY;
  }

  @Override
  public Block getShulker() {
    return ModBlocks.SHULKER;
  }

  @Override
  public BlockEntityType<?> getBarrelBlockEntity() {
    return ModBlockEntities.LOOTR_BARREL;
  }

  @Override
  public BlockEntityType<? extends ChestBlockEntity> getChestBlockEntity() {
    return ModBlockEntities.LOOTR_CHEST;
  }

  @Override
  public BlockEntityType<? extends ChestBlockEntity> getTrappedChestBlockEntity() {
    return ModBlockEntities.LOOTR_TRAPPED_CHEST;
  }

  @Override
  public BlockEntityType<? extends ChestBlockEntity> getInventoryBlockEntity() {
    return ModBlockEntities.LOOTR_INVENTORY;
  }

  @Override
  public BlockEntityType<?> getShulkerBlockEntity() {
    return ModBlockEntities.LOOTR_SHULKER;
  }

  @Override
  public Item getBarrelItem() {
    return ModItems.BARREL;
  }

  @Override
  public Item getChestItem() {
    return ModItems.CHEST;
  }

  @Override
  public Item getTrappedChestItem() {
    return ModItems.TRAPPED_CHEST;
  }

  @Override
  public Item getInventoryItem() {
    return ModItems.INVENTORY;
  }

  @Override
  public Item getTrophyItem() {
    return ModItems.TROPHY;
  }

  @Override
  public Item getShulkerItem() {
    return ModItems.SHULKER;
  }

  @Override
  public EntityType<?> getMinecart() {
    return ModEntities.LOOTR_MINECART_ENTITY;
  }

  @Override
  public IAdvancementTrigger getAdvancementTrigger() {
    return ModAdvancements.ADVANCEMENT;
  }

  @Override
  public IContainerTrigger getChestTrigger() {
    return ModAdvancements.CHEST;
  }

  @Override
  public IContainerTrigger getBarrelTrigger() {
    return ModAdvancements.BARREL;
  }

  @Override
  public IContainerTrigger getCartTrigger() {
    return ModAdvancements.CART;
  }

  @Override
  public IContainerTrigger getShulkerTrigger() {
    return ModAdvancements.SHULKER;
  }

  @Override
  public ILootedStatTrigger getStatTrigger() {
    return ModAdvancements.SCORE;
  }

  @Override
  public LootItemConditionType getLootCount() {
    return ModLoot.LOOT_COUNT;
  }

  @Override
  public Stat<?> getLootedStat() {
    if (ModStats.LOOTED_STAT == null) {
      ModStats.load();
    }
    return ModStats.LOOTED_STAT;
  }

  @Override
  public CreativeModeTab getTab() {
    return ModTabs.LOOTR_TAB;
  }
}
