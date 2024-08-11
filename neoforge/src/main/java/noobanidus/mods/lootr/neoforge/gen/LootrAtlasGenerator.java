package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.common.client.block.LootrShulkerBlockRenderer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class LootrAtlasGenerator extends SpriteSourceProvider {
  public LootrAtlasGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
    super(output, lookupProvider, LootrAPI.MODID, fileHelper);
  }

  @Override
  protected void gather() {
    this.atlas(CHESTS_ATLAS).addSource(new SingleFile(LootrChestBlockRenderer.MATERIAL.texture(), Optional.empty()));
    this.atlas(CHESTS_ATLAS).addSource(new SingleFile(LootrChestBlockRenderer.MATERIAL2.texture(), Optional.empty()));
    this.atlas(CHESTS_ATLAS).addSource(new SingleFile(LootrChestBlockRenderer.MATERIAL3.texture(), Optional.empty()));
    this.atlas(CHESTS_ATLAS).addSource(new SingleFile(LootrChestBlockRenderer.MATERIAL4.texture(), Optional.empty()));
    this.atlas(CHESTS_ATLAS).addSource(new SingleFile(LootrChestBlockRenderer.OLD_MATERIAL.texture(), Optional.empty()));
    this.atlas(CHESTS_ATLAS).addSource(new SingleFile(LootrChestBlockRenderer.OLD_MATERIAL2.texture(), Optional.empty()));
    this.atlas(SHULKER_BOXES_ATLAS).addSource(new SingleFile(LootrShulkerBlockRenderer.MATERIAL.texture(), Optional.empty()));
    this.atlas(SHULKER_BOXES_ATLAS).addSource(new SingleFile(LootrShulkerBlockRenderer.MATERIAL2.texture(), Optional.empty()));
    this.atlas(SHULKER_BOXES_ATLAS).addSource(new SingleFile(LootrShulkerBlockRenderer.MATERIAL3.texture(), Optional.empty()));
    this.atlas(SHULKER_BOXES_ATLAS).addSource(new SingleFile(LootrShulkerBlockRenderer.MATERIAL4.texture(), Optional.empty()));
    this.atlas(BLOCKS_ATLAS).addSource(new SingleFile(LootrAPI.rl("chest_opened"), Optional.empty()));
    this.atlas(BLOCKS_ATLAS).addSource(new SingleFile(LootrAPI.rl("minecraft", "entity/player/wide/steve"), Optional.empty()));
  }
}
