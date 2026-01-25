package noobanidus.mods.lootr.fabric.mixin.accessor;

import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SimpleParticleType.class)
public interface AccessorMixinSimpleParticleType {
  @Invoker(value="<init>")
  static SimpleParticleType lootr$invokeConstructor(boolean p_123456_) {
    throw new UnsupportedOperationException();
  }
}
