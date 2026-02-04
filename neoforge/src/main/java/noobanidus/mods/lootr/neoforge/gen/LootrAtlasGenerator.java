package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.AtlasIds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.data.SpriteSourceProvider;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.common.client.block.LootrShulkerBoxRenderer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class LootrAtlasGenerator extends SpriteSourceProvider {
  public LootrAtlasGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(output, lookupProvider, LootrAPI.MODID);
  }

  @Override
  protected void gather() {
    this.atlas(AtlasIds.CHESTS).addSource(new SingleFile(LootrChestBlockRenderer.MATERIAL.texture(), Optional.empty()));
    this.atlas(AtlasIds.CHESTS)
        .addSource(new SingleFile(LootrChestBlockRenderer.MATERIAL2.texture(), Optional.empty()));
    this.atlas(AtlasIds.CHESTS)
        .addSource(new SingleFile(LootrChestBlockRenderer.MATERIAL3.texture(), Optional.empty()));
    this.atlas(AtlasIds.CHESTS)
        .addSource(new SingleFile(LootrChestBlockRenderer.MATERIAL4.texture(), Optional.empty()));
    this.atlas(AtlasIds.SHULKER_BOXES)
        .addSource(new SingleFile(LootrShulkerBoxRenderer.MATERIAL.texture(), Optional.empty()));
    this.atlas(AtlasIds.SHULKER_BOXES)
        .addSource(new SingleFile(LootrShulkerBoxRenderer.MATERIAL2.texture(), Optional.empty()));
    this.atlas(AtlasIds.BLOCKS).addSource(new SingleFile(LootrAPI.rl("chest_opened"), Optional.empty()));
    this.atlas(AtlasIds.BLOCKS)
        .addSource(new SingleFile(LootrAPI.rl("minecraft", "entity/player/wide/steve"), Optional.empty()));
    this.atlas(Identifier.withDefaultNamespace("decorated_pot"))
        .addSource(new SingleFile(LootrAPI.rl("entity/loot_pot"), Optional.empty()))
        .addSource(new SingleFile(LootrAPI.rl("entity/loot_pot_open"), Optional.empty()))
/*        .addSource(new SingleFile(LootrAPI.rl("entity/decorated_pot/loot_pottery_pattern"), Optional.empty()))*/;
  }
}
