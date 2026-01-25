package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;
import noobanidus.mods.lootr.neoforge.init.ModParticles;

public class LootrParticleProvider extends ParticleDescriptionProvider {
  protected LootrParticleProvider(PackOutput output, ExistingFileHelper fileHelper) {
    super(output, fileHelper);
  }

  @Override
  protected void addDescriptions() {
    this.sprite(ModParticles.UNOPENED_PARTICLE.get(), ResourceLocation.withDefaultNamespace("glitter_0"));
  }
}
