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
    return ModBlocks.BARREL.get();
  }

  @Override
  public Block getChestBlock() {
    return ModBlocks.CHEST.get();
  }

  @Override
  public Block getTrappedChestBlock() {
    return ModBlocks.TRAPPED_CHEST.get();
  }

  @Override
  public Block getInventoryBlock() {
    return ModBlocks.INVENTORY.get();
  }

  @Override
  public Block getTrophyBlock() {
    return ModBlocks.TROPHY.get();
  }

  @Override
  public Block getShulker() {
    return ModBlocks.SHULKER.get();
  }

  @Override
  public BlockEntityType<?> getBarrelBlockEntity() {
    return ModBlockEntities.LOOTR_BARREL.get();
  }

  @Override
  public BlockEntityType<? extends ChestBlockEntity> getChestBlockEntity() {
    return ModBlockEntities.LOOTR_CHEST.get();
  }

  @Override
  public BlockEntityType<? extends ChestBlockEntity> getTrappedChestBlockEntity() {
    return ModBlockEntities.LOOTR_TRAPPED_CHEST.get();
  }

  @Override
  public BlockEntityType<? extends ChestBlockEntity> getInventoryBlockEntity() {
    return ModBlockEntities.LOOTR_INVENTORY.get();
  }

  @Override
  public BlockEntityType<?> getShulkerBlockEntity() {
    return ModBlockEntities.LOOTR_SHULKER.get();
  }

  @Override
  public Item getBarrelItem() {
    return ModItems.BARREL.get();
  }

  @Override
  public Item getChestItem() {
    return ModItems.CHEST.get();
  }

  @Override
  public Item getTrappedChestItem() {
    return ModItems.TRAPPED_CHEST.get();
  }

  @Override
  public Item getInventoryItem() {
    return ModItems.INVENTORY.get();
  }

  @Override
  public Item getTrophyItem() {
    return ModItems.TROPHY.get();
  }

  @Override
  public Item getShulkerItem() {
    return ModItems.SHULKER.get();
  }

  @Override
  public EntityType<?> getMinecart() {
    return ModEntities.LOOTR_MINECART_ENTITY.get();
  }

  @Override
  public IAdvancementTrigger getAdvancementTrigger() {
    return ModAdvancements.ADVANCEMENT.get();
  }

  @Override
  public IContainerTrigger getChestTrigger() {
    return ModAdvancements.CHEST.get();
  }

  @Override
  public IContainerTrigger getBarrelTrigger() {
    return ModAdvancements.BARREL.get();
  }

  @Override
  public IContainerTrigger getCartTrigger() {
    return ModAdvancements.CART.get();
  }

  @Override
  public IContainerTrigger getShulkerTrigger() {
    return ModAdvancements.SHULKER.get();
  }

  @Override
  public ILootedStatTrigger getStatTrigger() {
    return ModAdvancements.STAT.get();
  }

  @Override
  public LootItemConditionType getLootCount() {
    return ModLoot.LOOT_COUNT.get();
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
    return ModTabs.LOOTR.get();
  }
}
