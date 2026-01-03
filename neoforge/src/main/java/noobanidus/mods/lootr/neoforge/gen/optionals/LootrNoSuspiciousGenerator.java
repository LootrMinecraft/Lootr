package noobanidus.mods.lootr.neoforge.gen.optionals;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class LootrNoSuspiciousGenerator {
  public static class LootrBlockTagProvider extends BlockTagsProvider {
    public LootrBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, lookupProvider, LootrAPI.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
      tag(LootrTags.Blocks.CONVERT_SANDS).replace();
      tag(LootrTags.Blocks.CONVERT_GRAVELS).replace();
    }

    @Override
    public String getName() {
      return "(Optional) Remove Lootr Suspicious Block Tags";
    }
  }
}
