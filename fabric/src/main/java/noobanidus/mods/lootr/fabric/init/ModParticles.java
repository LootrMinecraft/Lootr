package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.particle.ParticleColorOption;
import noobanidus.mods.lootr.fabric.mixin.accessor.AccessorMixinSimpleParticleType;

public class ModParticles {
  public static final ParticleType<ParticleColorOption> UNOPENED_PARTICLE = ParticleColorOption.create(true);
  public static final SimpleParticleType REFRESH_PARTICLE = AccessorMixinSimpleParticleType.lootr$invokeConstructor(true);

  public static void register() {
    Registry.register(BuiltInRegistries.PARTICLE_TYPE, LootrConstants.Identifiers.UNOPENED_PARTICLE, UNOPENED_PARTICLE);
    Registry.register(BuiltInRegistries.PARTICLE_TYPE, LootrConstants.Identifiers.REFRESH_PARTICLE, REFRESH_PARTICLE);
  }
}
