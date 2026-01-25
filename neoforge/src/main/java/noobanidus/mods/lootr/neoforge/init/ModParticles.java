package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;

public class ModParticles {
  private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, LootrAPI.MODID);

  public static final DeferredHolder<ParticleType<?>, SimpleParticleType> UNOPENED_PARTICLE = PARTICLES.register(LootrConstants.UNOPENED_PARTICLE.getPath(), () -> new SimpleParticleType(true));

  public static void register (IEventBus bus) {
    PARTICLES.register(bus);
  }
}
