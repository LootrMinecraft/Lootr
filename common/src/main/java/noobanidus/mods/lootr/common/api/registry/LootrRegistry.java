package noobanidus.mods.lootr.common.api.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import noobanidus.mods.lootr.common.api.advancement.IAdvancementTrigger;
import noobanidus.mods.lootr.common.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.advancement.ILootedStatTrigger;

/**
 * Platform-independent way to access registered objects.
 */
public final class LootrRegistry {
  public static ILootrRegistry INSTANCE = null;

  public static boolean isReady() {
    return INSTANCE != null;
  }

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
    return INSTANCE.getShulkerBlock();
  }

  public static Block getDecoratedPotBlock() {
    return INSTANCE.getDecoratedPotBlock();
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

  public static Item getDecoratedPotItem() {
    return INSTANCE.getDecoratedPotItem();
  }

  public static EntityType<?> getMinecart() {
    return INSTANCE.getMinecart();
  }

  public static EntityType<? extends ItemFrame> getItemFrame() {
    return INSTANCE.getItemFrame();
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

  public static BlockEntityType<?> getDecoratedPotBlockEntity() {
    return INSTANCE.getDecoratedPotBlockEntity();
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

  public static IContainerTrigger getSandTrigger() {
    return INSTANCE.getSandTrigger();
  }

  public static IContainerTrigger getGravelTrigger() {
    return INSTANCE.getGravelTrigger();
  }

  public static ILootedStatTrigger getStatTrigger() {
    return INSTANCE.getStatTrigger();
  }

  public static IContainerTrigger getPotTrigger () {
    return INSTANCE.getPotTrigger();
  }

  public static IContainerTrigger getItemFrameTrigger () {
    return INSTANCE.getItemFrameTrigger();
  }

  public static Stat<?> getLootedStat() {
    return INSTANCE.getLootedStat();
  }

  public static CreativeModeTab getTab() {
    return INSTANCE.getTab();
  }

  public static BlockEntityType<?> getBrushableBlockEntity() {
    return INSTANCE.getBrushableBlockEntity();
  }

  public static Block getSuspiciousSandBlock() {
    return INSTANCE.getSuspiciousSandBlock();
  }

  public static Block getSuspiciousGravelBlock() {
    return INSTANCE.getSuspiciousGravelBlock();
  }

  public static Item getSuspiciousGravelItem() {
    return INSTANCE.getSuspiciousGravelItem();
  }

  public static Item getSuspiciousSandItem() {
    return INSTANCE.getSuspiciousSandItem();
  }

  public static SimpleParticleType getUnopenedParticleType () {
    return INSTANCE.getUnopenedParticleType ();
  }
}
