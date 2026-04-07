package noobanidus.mods.lootr.common.api.interfaces.lootr;

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
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ILootrRegistry {
  Block getBarrelBlock();

  Block getChestBlock();

  Block getTrappedChestBlock();

  Block getInventoryBlock();

  Block getTrophyBlock();

  default Block getShulker() {
    return getShulkerBoxBlock();
  }

  Block getShulkerBoxBlock();

  Block getSuspiciousSandBlock();

  Block getSuspiciousGravelBlock();

  Block getDecoratedPotBlock();

  Item getBarrelItem();

  Item getChestItem();

  Item getTrappedChestItem();

  Item getInventoryItem();

  Item getTrophyItem();

  Item getShulkerBoxItem();

  Item getSuspiciousSandItem();

  Item getSuspiciousGravelItem();

  Item getDecoratedPotItem();

  EntityType<?> getMinecart();

  EntityType<? extends ItemFrame> getItemFrame();

  BlockEntityType<?> getBrushableBlockEntity();

  BlockEntityType<?> getBarrelBlockEntity();

  BlockEntityType<? extends ChestBlockEntity> getChestBlockEntity();

  BlockEntityType<? extends ChestBlockEntity> getTrappedChestBlockEntity();

  BlockEntityType<? extends ChestBlockEntity> getInventoryBlockEntity();

  BlockEntityType<?> getShulkerBlockEntity();

  BlockEntityType<?> getDecoratedPotBlockEntity();

  IAdvancementTrigger getAdvancementTrigger();

  IContainerTrigger getChestTrigger();

  IContainerTrigger getBarrelTrigger();

  IContainerTrigger getCartTrigger();

  IContainerTrigger getShulkerTrigger();

  ILootedStatTrigger getStatTrigger();

  IContainerTrigger getSandTrigger();

  IContainerTrigger getGravelTrigger();

  IContainerTrigger getPotTrigger();

  Stat<?> getLootedStat();

  CreativeModeTab getTab();

  IContainerTrigger getItemFrameTrigger();

  SimpleParticleType getUnopenedParticleType();
}
