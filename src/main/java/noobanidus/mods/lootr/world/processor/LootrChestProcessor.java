package noobanidus.mods.lootr.world.processor;

import com.mojang.serialization.Codec;
import net.minecraft.block.*;
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
  protected IStructureProcessorType<?> getType() {
    return ModMisc.LOOTR_PROCESSOR;
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
      RegistryKey<World> key = ((IServerWorld) world).getLevel().dimension();
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

    return new Template.BlockInfo(info2.pos, replacement, info2.nbt);
  }
}
