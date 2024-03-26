package noobanidus.mods.lootr.gen;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.client.block.LootrShulkerBlockRenderer;

import java.util.Optional;

public class LootrAtlasGenerator extends SpriteSourceProvider {
  public LootrAtlasGenerator(PackOutput output, ExistingFileHelper fileHelper) {
    super(output, fileHelper, LootrAPI.MODID);
  }

  @Override
  protected void addSources() {
    this.atlas(CHESTS_ATLAS).addSource(new SingleFile(LootrChestBlockRenderer.MATERIAL.texture(), Optional.empty()));
    this.atlas(CHESTS_ATLAS).addSource(new SingleFile(LootrChestBlockRenderer.MATERIAL2.texture(), Optional.empty()));
    this.atlas(SHULKER_BOXES_ATLAS).addSource(new SingleFile(LootrShulkerBlockRenderer.MATERIAL.texture(), Optional.empty()));
    this.atlas(SHULKER_BOXES_ATLAS).addSource(new SingleFile(LootrShulkerBlockRenderer.MATERIAL2.texture(), Optional.empty()));
    this.atlas(BLOCKS_ATLAS).addSource(new SingleFile(new ResourceLocation(LootrAPI.MODID, "chest_opened"), Optional.empty()));
    this.atlas(BLOCKS_ATLAS).addSource(new SingleFile(new ResourceLocation("minecraft", "entity/player/wide/steve"), Optional.empty()));
  }
}
