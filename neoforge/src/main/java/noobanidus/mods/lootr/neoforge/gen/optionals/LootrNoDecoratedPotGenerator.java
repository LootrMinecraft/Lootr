package noobanidus.mods.lootr.neoforge.gen.optionals;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;

import java.util.concurrent.CompletableFuture;

public class LootrNoDecoratedPotGenerator {
  public static class LootrBlockTagProvider extends BlockTagsProvider {
    public LootrBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
      super(output, lookupProvider, LootrAPI.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
      tag(LootrTags.Blocks.CONVERT_POTS).replace();
    }

    @Override
    public String getName() {
      return "(Optional) Remove Lootr Decorated Pot Block Tags";
    }
  }
}
