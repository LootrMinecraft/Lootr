package noobanidus.mods.lootr.init;

import com.google.common.collect.Sets;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.block.*;
import noobanidus.mods.lootr.block.entities.LootrShulkerBlockEntity;

import java.util.Set;

public class ModBlocks {
  private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, LootrAPI.MODID);

  private static final BlockBehaviour.StatePredicate posPredicate = (state, level, pos) -> {
    BlockEntity blockentity = level.getBlockEntity(pos);
    if (blockentity instanceof LootrShulkerBlockEntity shulkerboxblockentity) {
      return shulkerboxblockentity.isClosed();
    } else {
      return false;
    }
  };

  public static final RegistryObject<LootrBarrelBlock> BARREL = REGISTER.register("lootr_barrel", () -> new LootrBarrelBlock(BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.5f)));
  public static final RegistryObject<LootrChestBlock> CHEST = REGISTER.register("lootr_chest", () -> new LootrChestBlock(BlockBehaviour.Properties.copy(Blocks.BARREL).strength(2.5f)));
  public static final RegistryObject<LootrTrappedChestBlock> TRAPPED_CHEST = REGISTER.register("lootr_trapped_chest", () -> new LootrTrappedChestBlock(BlockBehaviour.Properties.copy(Blocks.TRAPPED_CHEST).strength(2.5f)));
  public static final RegistryObject<LootrInventoryBlock> INVENTORY = REGISTER.register("lootr_inventory", () -> new LootrInventoryBlock(Block.Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD)));
  public static final RegistryObject<LootrShulkerBlock> SHULKER = REGISTER.register("lootr_shulker", () -> new LootrShulkerBlock(Block.Properties.of(Material.SHULKER_SHELL).strength(2.5f).dynamicShape().noOcclusion().isSuffocating(posPredicate).isViewBlocking(posPredicate)));
  public static final RegistryObject<Block> TROPHY = REGISTER.register("trophy", () -> new TrophyBlock(Block.Properties.of(Material.METAL).strength(15f).sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15)));

  private static Set<Block> specialLootChests = null;

  // TODO: Migrate this to a tag
  public static Set<Block> getSpecialLootChests () {
   if (specialLootChests == null) {
    specialLootChests = Sets.newHashSet(CHEST.get(), BARREL.get(), TRAPPED_CHEST.get(), SHULKER.get(), INVENTORY.get());
    }

   return specialLootChests;
  }

  public static void register (IEventBus bus) {
    REGISTER.register(bus);
  }
}
