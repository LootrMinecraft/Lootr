package noobanidus.mods.lootr.world.processor;

import com.mojang.serialization.Codec;
import net.minecraft.block.*;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModMisc;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("NullableProblems")
public class LootrChestProcessor extends StructureProcessor {
  public static final LootrChestProcessor INSTANCE = new LootrChestProcessor();
  public static final Codec<LootrChestProcessor> CODEC = Codec.unit(() -> INSTANCE);

  @Override
  protected IStructureProcessorType<?> getType() {
    return ModMisc.LOOTR_PROCESSOR;
  }

  private static Map<Block, Block> replacements = null;

  private static final List<ResourceLocation> QUARK_CHESTS = Arrays.asList(new ResourceLocation("quark", "oak_chest"), new ResourceLocation("quark", "spruce_chest"), new ResourceLocation("quark", "birch_chest"), new ResourceLocation("quark", "jungle_chest"), new ResourceLocation("quark", "acacia_chest"), new ResourceLocation("quark", "dark_oak_chest"), new ResourceLocation("quark", "warped_chest"), new ResourceLocation("quark", "crimson_chest")); // Quark normal chests
  private static final List<ResourceLocation> QUARK_TRAPPED_CHESTS = Arrays.asList(new ResourceLocation("quark", "oak_trapped_chest"), new ResourceLocation("quark", "spruce_trapped_chest"), new ResourceLocation("quark", "birch_trapped_chest"), new ResourceLocation("quark", "jungle_trapped_chest"), new ResourceLocation("quark", "acacia_trapped_chest"), new ResourceLocation("quark", "dark_oak_trapped_chest"), new ResourceLocation("quark", "warped_trapped_chest"), new ResourceLocation("quark", "crimson_trapped_chest"));

  private static void addReplacement(ResourceLocation location, Block replacement) {
    Block block = ForgeRegistries.BLOCKS.getValue(location);
    if (block != null) {
      replacements.put(block, replacement);
    }
  }

  // TODO: Move this to the config module?
  public static BlockState replacement(BlockState original) {
    if (replacements == null) {
      replacements = new HashMap<>();
      replacements.put(Blocks.CHEST, ModBlocks.CHEST);
      replacements.put(Blocks.BARREL, ModBlocks.BARREL);
      replacements.put(Blocks.TRAPPED_CHEST, ModBlocks.TRAPPED_CHEST);
      if (ConfigManager.CONVERT_QUARK.get() && ModList.get().isLoaded("quark")) {
        QUARK_CHESTS.forEach(o -> addReplacement(o, ModBlocks.CHEST));
        QUARK_TRAPPED_CHESTS.forEach(o -> addReplacement(o, ModBlocks.TRAPPED_CHEST));
      }
      if (ConfigManager.CONVERT_WOODEN_CHESTS.get() || ConfigManager.CONVERT_TRAPPED_CHESTS.get()) {
        final ServerWorld world = ServerLifecycleHooks.getCurrentServer().getWorld(World.OVERWORLD);
        if (ConfigManager.CONVERT_WOODEN_CHESTS.get()) {
          Tags.Blocks.CHESTS_WOODEN.getAllElements().forEach(o -> {
            if (replacements.containsKey(o)) {
              return;
            }
            TileEntity tile = o.createTileEntity(o.getDefaultState(), world);
            if (tile instanceof LockableLootTileEntity) {
              replacements.put(o, ModBlocks.CHEST);
            }
          });
        }
        if (ConfigManager.CONVERT_TRAPPED_CHESTS.get()) {
          Tags.Blocks.CHESTS_TRAPPED.getAllElements().forEach(o -> {
            if (replacements.containsKey(o)) {
              return;
            }
            TileEntity tile = o.createTileEntity(o.getDefaultState(), world);
            if (tile instanceof LockableLootTileEntity) {
              replacements.put(o, ModBlocks.CHEST);
            }
          });
        }
      }
    }

    Block replacement = replacements.get(original.getBlock());
    if (replacement == null) {
      return null;
    }

    BlockState newState = replacement.getDefaultState();
    if (replacement == ModBlocks.CHEST || replacement == ModBlocks.TRAPPED_CHEST) {
      newState = newState.with(ChestBlock.FACING, original.get(ChestBlock.FACING)).with(ChestBlock.WATERLOGGED, original.get(ChestBlock.WATERLOGGED));
    } else if (replacement == ModBlocks.BARREL) {
      newState = newState.with(BarrelBlock.PROPERTY_OPEN, original.get(BarrelBlock.PROPERTY_OPEN)).with(BarrelBlock.PROPERTY_FACING, original.get(BarrelBlock.PROPERTY_FACING));
    }
    return newState;
  }

  @Nullable
  public Template.BlockInfo process(IWorldReader world, BlockPos pos, BlockPos blockPos, Template.BlockInfo info1, Template.BlockInfo info2, PlacementSettings placement, @Nullable Template template) {
    if (info2.nbt == null || !info2.nbt.contains("LootTable", Constants.NBT.TAG_STRING)) {
      return info2;
    }

    ResourceLocation table = new ResourceLocation(info2.nbt.getString("LootTable"));
    if (ConfigManager.getLootBlacklist().contains(table)) {
      return info2;
    }

    if (world instanceof IServerWorld) {
      RegistryKey<World> key = ((IServerWorld) world).getWorld().getDimensionKey();
      if (!ConfigManager.getDimensionWhitelist().contains(key) || ConfigManager.getDimensionBlacklist().contains(key)) {
        return info2;
      }
    } else {
      Lootr.LOG.info("Unable to determine dimension while converting NBT template with loot table '" + table + "': dimension blacklist/whitelisting may not have been applied.");
    }

    BlockState state = info2.state;
    BlockState replacement = replacement(state);
    if (replacement == null) {
      return info2;
    }

    return new Template.BlockInfo(info2.pos, replacement, info2.nbt);
  }
}
