package noobanidus.mods.lootr.common.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class RefreshParticle extends SingleQuadParticle {

  private final float driftX;
  private final float driftZ;

  public RefreshParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, TextureAtlasSprite sprite, RandomSource random) {
    super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite);
    this.lifetime = 30;
    this.alpha = 0.8f;
    this.xd = xSpeed;
    this.yd = ySpeed;
    this.zd = zSpeed;
    this.hasPhysics = false;
    this.quadSize = 0.12f;
    this.friction = 1f;
    int c1 = 0xfad64a;
    this.rCol = ((c1 >> 16) & 0xFF) / 255.0f;
    this.gCol = ((c1 >> 8) & 0xFF) / 255.0f;
    this.bCol = ((c1) & 0xFF) / 255.0f;

    // Random arc direction — small seed that grows over time in tick()
    float angle = random.nextFloat() * (float) (Math.PI * 2);
    float radius = 0.008f + random.nextFloat() * 0.012f; // controls arc width
    this.driftX = (float) Math.cos(angle) * radius;
    this.driftZ = (float) Math.sin(angle) * radius;
  }

  @Override
  protected int getLightCoords(float a) {
    return 0xf000f0 | super.getLightCoords(a) & 0xff0000;
  }

  @Override
  public void tick() {
    super.tick();
    if (!this.removed) {
      // Age ratio from 0→1; use it to accelerate the outward drift (ease-in curve)
      float f = (float) this.age / (float) this.lifetime;
      this.xd += driftX * f;
      this.zd += driftZ * f;

      float fade = f * f;
      this.alpha = Math.max(0, 0.8f - fade);
    }
  }

  @Override
  protected Layer getLayer() {
    return Layer.TRANSLUCENT;
  }

  public record Provider(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {
    @Override
    public @Nullable Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
      return new RefreshParticle(level, x, y, z, 0, ySpeed, 0, spriteSet.get(random), random);
    }
  }
}
