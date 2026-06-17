package noobanidus.mods.lootr.fabric.init;

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;
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
    Registry.register(BuiltInRegistries.PARTICLE_TYPE, LootrConstants.UNOPENED_PARTICLE, UNOPENED_PARTICLE);
    Registry.register(BuiltInRegistries.PARTICLE_TYPE, LootrConstants.REFRESH_PARTICLE, REFRESH_PARTICLE);
  }
}
