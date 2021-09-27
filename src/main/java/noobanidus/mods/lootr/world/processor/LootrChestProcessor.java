package noobanidus.mods.lootr.world.processor;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.common.util.Constants;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModMisc;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class LootrChestProcessor extends StructureProcessor {
  public static final LootrChestProcessor INSTANCE = new LootrChestProcessor();
  public static final Codec<LootrChestProcessor> CODEC = Codec.unit(() -> INSTANCE);

  @Override
  protected StructureProcessorType<?> getType() {
    return ModMisc.LOOTR_PROCESSOR;
  }

  @Nullable
  public StructureTemplate.StructureBlockInfo process(LevelReader world, BlockPos pos, BlockPos blockPos, StructureTemplate.StructureBlockInfo info1, StructureTemplate.StructureBlockInfo info2, StructurePlaceSettings placement, @Nullable StructureTemplate template) {
    if (info2.nbt == null || !info2.nbt.contains("LootTable", Constants.NBT.TAG_STRING)) {
      return info2;
    }

    ResourceLocation table = new ResourceLocation(info2.nbt.getString("LootTable"));
    if (ConfigManager.getLootBlacklist().contains(table)) {
      return info2;
    }

    if (world instanceof ServerLevelAccessor) {
      ResourceKey<Level> key = ((ServerLevelAccessor) world).getLevel().dimension();
      if (ConfigManager.isDimensionBlocked(key)) {
        return info2;
      }
    } else {
      Lootr.LOG.info("Unable to determine dimension while converting NBT template with loot table '" + table + "': dimension blacklist/whitelisting may not have been applied.");
    }

    BlockState state = info2.state;
    BlockState replacement = ConfigManager.replacement(state);
    if (replacement == null) {
      return info2;
    }

    return new StructureTemplate.StructureBlockInfo(info2.pos, replacement, info2.nbt);
  }
}
