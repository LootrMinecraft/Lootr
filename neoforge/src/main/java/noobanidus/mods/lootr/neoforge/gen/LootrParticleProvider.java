package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.data.ParticleDescriptionProvider;
import noobanidus.mods.lootr.neoforge.init.ModParticles;

public class LootrParticleProvider extends ParticleDescriptionProvider {
  protected LootrParticleProvider(PackOutput output) {
    super(output);
  }

  @Override
  protected void addDescriptions() {
    this.spriteSet(ModParticles.UNOPENED_PARTICLE.get(), Identifier.withDefaultNamespace("glitter_0"));
  }
}
