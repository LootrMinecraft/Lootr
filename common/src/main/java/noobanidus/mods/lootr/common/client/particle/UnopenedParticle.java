package noobanidus.mods.lootr.common.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import noobanidus.mods.lootr.common.api.particle.ParticleColorOption;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class UnopenedParticle extends SingleQuadParticle {
  public UnopenedParticle(ClientLevel level, ParticleColorOption type, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, TextureAtlasSprite sprite) {
    super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite);
    this.lifetime = 30;
    this.alpha = 0.8f;
    this.xd = 0;
    this.yd = ySpeed;
    this.zd = 0;
    this.hasPhysics = false;
    this.quadSize = 0.12f;
    this.friction = 1f;

    int c1 = type.color1();
    this.rCol = ((c1 >> 16) & 0xFF) / 255.0f;
    this.gCol = ((c1 >> 8) & 0xFF) / 255.0f;
    this.bCol = ((c1) & 0xFF) / 255.0f;
  }

  @Override
  protected int getLightCoords(float a) {
    return 0xf000f0 | super.getLightCoords(a) & 0xff0000;
  }

  @Override
  public void tick() {
    super.tick();
    if (!this.removed) {
      float f = (float) this.age / (float) this.lifetime;
      f *= f;
      this.alpha = Math.max(0, 0.8f - f);
    }
  }

  @Override
  protected @NonNull Layer getLayer() {
    return Layer.TRANSLUCENT;
  }

  public record Provider(SpriteSet spriteSet) implements ParticleProvider<ParticleColorOption> {
    @Override
    public @Nullable Particle createParticle(ParticleColorOption particleType, @NonNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @NonNull RandomSource random) {
      return new UnopenedParticle(level, particleType, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet.get(random));
    }
  }
}
