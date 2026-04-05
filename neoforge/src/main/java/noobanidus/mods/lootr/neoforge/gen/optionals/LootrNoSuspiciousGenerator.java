package noobanidus.mods.lootr.neoforge.gen.optionals;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class LootrNoSuspiciousGenerator {
  public static class LootrBlockTagProvider extends BlockTagsProvider {
    public LootrBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
      super(output, lookupProvider, LootrAPI.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
      tag(LootrTags.Blocks.CONVERT_SANDS).replace();
      tag(LootrTags.Blocks.CONVERT_GRAVELS).replace();
    }

    @Override
    public @NonNull String getName() {
      return "(Optional) Remove Lootr Suspicious Block Tags";
    }
  }
}
