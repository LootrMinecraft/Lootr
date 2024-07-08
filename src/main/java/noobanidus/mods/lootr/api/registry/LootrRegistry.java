package noobanidus.mods.lootr.api.registry;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import noobanidus.mods.lootr.api.advancement.IAdvancementTrigger;
import noobanidus.mods.lootr.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.api.advancement.ILootedStatTrigger;

public class LootrRegistry {
  public static ILootrRegistry INSTANCE;

  public static Block getBarrel() {
    return INSTANCE.getBarrel();
  }

  public static Block getChest() {
    return INSTANCE.getChest();
  }

  public static Block getTrappedChest() {
    return INSTANCE.getTrappedChest();
  }

  public static Block getInventory() {
    return INSTANCE.getInventory();
  }

  public static Block getTrophy() {
    return INSTANCE.getTrophy();
  }

  public static Block getShulker() {
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

  public static BlockEntityType<?> getChestBlockEntity() {
    return INSTANCE.getChestBlockEntity();
  }

  public static BlockEntityType<?> getTrappedChestBlockEntity() {
    return INSTANCE.getTrappedChestBlockEntity();
  }

  public static BlockEntityType<?> getInventoryBlockEntity() {
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
