package noobanidus.mods.lootr.common.api;

import net.minecraft.core.particles.ParticleType;
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
import noobanidus.mods.lootr.common.api.particle.ParticleColorOption;

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

  public static Block getTrophyBlock() {
    return INSTANCE.getTrophyBlock();
  }

  public static Block getShulkerBoxBlock() {
    return INSTANCE.getShulkerBoxBlock();
  }

  public static Block getDecoratedPotBlock() {
    return INSTANCE.getDecoratedPotBlock();
  }

  public static Block getCopperChestBlock () {
    return INSTANCE.getCopperChestBlock();
  }

  public static Block getExposedCopperChestBlock () {
    return INSTANCE.getExposedCopperChestBlock();
  }

  public static Block getOxidizedCopperChestBlock () {
    return INSTANCE.getOxidizedCopperChestBlock();
  }

  public static Block getWeatheredCopperChestBlock () {
    return INSTANCE.getWeatheredCopperChestBlock();
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

  public static Item getTrophyItem() {
    return INSTANCE.getTrophyItem();
  }

  public static Item getShulkerBoxItem() {
    return INSTANCE.getShulkerBoxItem();
  }

  public static Item getDecoratedPotItem() {
    return INSTANCE.getDecoratedPotItem();
  }

  public static Item getCopperChestItem () {
    return INSTANCE.getCopperChestItem();
  }

  public static Item getExposedCopperChestItem () {
    return INSTANCE.getExposedCopperChestItem();
  }

  public static Item getOxidizedCopperChestItem () {
    return INSTANCE.getOxidizedCopperChestItem();
  }

  public static Item getWeatheredCopperChestItem () {
    return INSTANCE.getWeatheredCopperChestItem();
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

  public static BlockEntityType<?> getShulkerBoxBlockEntity() {
    return INSTANCE.getShulkerBoxBlockEntity();
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

  public static IContainerTrigger getCopperChestTrigger () {
    return INSTANCE.getCopperChestTrigger();
  }

  public static IContainerTrigger getExposedCopperChestTrigger () {
    return INSTANCE.getExposedCopperChestTrigger();
  }

  public static IContainerTrigger getWeatheredCopperChestTrigger () {
    return INSTANCE.getWeatheredCopperChestTrigger();
  }

  public static IContainerTrigger getOxidizedCopperChestTrigger () {
    return INSTANCE.getOxidizedCopperChestTrigger();
  }

  public static IContainerTrigger getTrappedChestTrigger () {
    return INSTANCE.getTrappedChestTrigger();
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

  public static ParticleType<ParticleColorOption> getUnopenedParticleType () {
    return INSTANCE.getUnopenedParticleType ();
  }
}
