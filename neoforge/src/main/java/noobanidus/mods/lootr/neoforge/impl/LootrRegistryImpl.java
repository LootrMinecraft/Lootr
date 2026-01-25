package noobanidus.mods.lootr.neoforge.impl;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import noobanidus.mods.lootr.common.api.advancement.IAdvancementTrigger;
import noobanidus.mods.lootr.common.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.advancement.ILootedStatTrigger;
import noobanidus.mods.lootr.common.api.registry.ILootrRegistry;
import noobanidus.mods.lootr.neoforge.init.*;

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
  public Block getShulkerBlock() {
    return ModBlocks.SHULKER.get();
  }

  @Override
  public Block getSuspiciousSandBlock() {
    return ModBlocks.SUSPICIOUS_SAND.get();
  }

  @Override
  public Block getSuspiciousGravelBlock() {
    return ModBlocks.SUSPICIOUS_GRAVEL.get();
  }

  @Override
  public Block getDecoratedPotBlock() {
    return ModBlocks.DECORATED_POT.get();
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
  public BlockEntityType<?> getDecoratedPotBlockEntity() {
    return ModBlockEntities.LOOTR_DECORATED_POT.get();
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
  public Item getSuspiciousSandItem() {
    return ModItems.SUSPICIOUS_SAND.get();
  }

  @Override
  public Item getSuspiciousGravelItem() {
    return ModItems.SUSPICIOUS_GRAVEL.get();
  }

  @Override
  public Item getDecoratedPotItem() {
    return ModItems.DECORATED_POT.get();
  }

  @Override
  public EntityType<?> getMinecart() {
    return ModEntities.LOOTR_MINECART_ENTITY.get();
  }

  @Override
  public EntityType<? extends ItemFrame> getItemFrame() {
    return ModEntities.ITEM_FRAME.get();
  }

  @Override
  public BlockEntityType<?> getBrushableBlockEntity() {
    return ModBlockEntities.LOOTR_BRUSHABLE_BLOCK.get();
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
  public IContainerTrigger getSandTrigger() {
    return ModAdvancements.SAND.get();
  }

  @Override
  public IContainerTrigger getGravelTrigger() {
    return ModAdvancements.GRAVEL.get();
  }

  @Override
  public IContainerTrigger getPotTrigger() {
    return ModAdvancements.POT.get();
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

  @Override
  public IContainerTrigger getItemFrameTrigger() {
    return ModAdvancements.ITEM_FRAME.get();
  }

  @Override
  public SimpleParticleType getUnopenedParticleType() {
    return ModParticles.UNOPENED_PARTICLE.get();
  }
}
