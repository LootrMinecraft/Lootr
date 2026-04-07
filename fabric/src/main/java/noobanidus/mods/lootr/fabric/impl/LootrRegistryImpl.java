package noobanidus.mods.lootr.fabric.impl;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import noobanidus.mods.lootr.common.api.interfaces.advancement.IAdvancementTrigger;
import noobanidus.mods.lootr.common.api.interfaces.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.interfaces.advancement.ILootedStatTrigger;
import noobanidus.mods.lootr.common.api.interfaces.lootr.ILootrRegistry;
import noobanidus.mods.lootr.fabric.init.*;

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
  public Block getShulkerBoxBlock() {
    return ModBlocks.SHULKER_BOX;
  }

  @Override
  public Block getSuspiciousSandBlock() {
    return ModBlocks.SUSPICIOUS_SAND;
  }

  @Override
  public Block getSuspiciousGravelBlock() {
    return ModBlocks.SUSPICIOUS_GRAVEL;
  }

  @Override
  public Block getDecoratedPotBlock() {
    return ModBlocks.DECORATED_POT;
  }

  @Override
  public BlockEntityType<?> getBarrelBlockEntity() {
    return ModBlockEntities.BARREL;
  }

  @Override
  public BlockEntityType<? extends ChestBlockEntity> getChestBlockEntity() {
    return ModBlockEntities.CHEST;
  }

  @Override
  public BlockEntityType<? extends ChestBlockEntity> getTrappedChestBlockEntity() {
    return ModBlockEntities.TRAPPED_CHEST;
  }

  @Override
  public BlockEntityType<? extends ChestBlockEntity> getInventoryBlockEntity() {
    return ModBlockEntities.INVENTORY;
  }

  @Override
  public BlockEntityType<?> getShulkerBoxBlockEntity() {
    return ModBlockEntities.SHULKER_BOX;
  }

  @Override
  public BlockEntityType<?> getDecoratedPotBlockEntity() {
    return ModBlockEntities.DECORATED_POT;
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
  public Item getShulkerBoxItem() {
    return ModItems.SHULKER_BOX;
  }

  @Override
  public Item getSuspiciousSandItem() {
    return ModItems.SUSPICIOUS_SAND;
  }

  @Override
  public Item getSuspiciousGravelItem() {
    return ModItems.SUSPICIOUS_GRAVEL;
  }

  @Override
  public Item getDecoratedPotItem() {
    return ModItems.DECORATED_POT;
  }

  @Override
  public EntityType<?> getMinecart() {
    return ModEntities.MINECART_WITH_CHEST;
  }

  @Override
  public EntityType<? extends ItemFrame> getItemFrame() {
    return ModEntities.ITEM_FRAME;
  }

  @Override
  public BlockEntityType<?> getBrushableBlockEntity() {
    return ModBlockEntities.BRUSHABLE_BLOCK;
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
  public IContainerTrigger getSandTrigger() {
    return ModAdvancements.SAND;
  }

  @Override
  public IContainerTrigger getGravelTrigger() {
    return ModAdvancements.GRAVEL;
  }

  @Override
  public IContainerTrigger getPotTrigger() {
    return ModAdvancements.POT;
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

  @Override
  public IContainerTrigger getItemFrameTrigger() {
    return ModAdvancements.ITEM_FRAME;
  }

  @Override
  public SimpleParticleType getUnopenedParticleType() {
    return ModParticles.UNOPENED_PARTCLE;
  }
}
