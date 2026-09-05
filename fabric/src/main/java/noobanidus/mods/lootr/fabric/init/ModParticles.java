package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.particle.ParticleColorOption;

public class ModParticles {
  public static final ParticleType<ParticleColorOption> UNOPENED_PARTCLE = ParticleColorOption.create(false);

  public static void register() {
    Registry.register(BuiltInRegistries.PARTICLE_TYPE, LootrConstants.UNOPENED_PARTICLE, UNOPENED_PARTCLE);
  }
}
