package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;

import java.util.concurrent.CompletableFuture;

public class LootrStructureTagsProvider extends IntrinsicHolderTagsProvider<Structure> {
  public LootrStructureTagsProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
    super(arg, Registries.STRUCTURE, completableFuture, (Structure arg2) -> null, LootrAPI.MODID);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(LootrTags.Structure.STRUCTURE_BLACKLIST); //.add(BuiltinStructures.DESERT_PYRAMID);
    tag(LootrTags.Structure.STRUCTURE_WHITELIST); //.add(BuiltinStructures.DESERT_PYRAMID);
    tag(LootrTags.Structure.DECAY_STRUCTURES); //.add(BuiltinStructures.DESERT_PYRAMID);
    tag(LootrTags.Structure.REFRESH_STRUCTURES); //.add(BuiltinStructures.JUNGLE_TEMPLE);
  }

  @Override
  public String getName() {
    return "Lootr Structure Tags";
  }
}