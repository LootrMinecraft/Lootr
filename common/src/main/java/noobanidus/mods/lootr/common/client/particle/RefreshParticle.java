package noobanidus.mods.lootr.common.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import noobanidus.mods.lootr.common.particle.ParticleColorOption;

public class RefreshParticle extends TextureSheetParticle {
  private final double startX, startZ;
  private static final float RADIUS = 0.3f;
  private static final float ANGULAR_SPEED = 0.1f;
  private final float angleOffset;

  public RefreshParticle(ClientLevel level, ParticleColorOption type, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    super(level, x, y, z, xSpeed, ySpeed, zSpeed);
    this.startX = x;
    this.startZ = z;
    this.angleOffset = this.random.nextFloat() * Mth.TWO_PI;

    this.lifetime = 60;
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

    updateSpiralPosition();
    this.xo = this.x;
    this.zo = this.z;
  }

  private void updateSpiralPosition() {
    float angle = this.angleOffset + this.age * ANGULAR_SPEED;
    this.setPos(
        this.startX + Mth.cos(angle) * RADIUS,
        this.y,
        this.startZ + Mth.sin(angle) * RADIUS
    );
  }

  @Override
  protected int getLightColor(float partialTick) {
    return 0xf000f0 | super.getLightColor(partialTick) & 0xff0000;
  }

  @Override
  public void tick() {
    super.tick();
    if (!this.removed) {
      float f = (float) this.age / (float) this.lifetime;
      f *= f;
      this.alpha = Math.max(0, 0.8f - f);
      updateSpiralPosition();
    }
  }

  @Override
  public ParticleRenderType getRenderType() {
    return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
  }

  public record Provider(SpriteSet spriteSet) implements ParticleProvider<ParticleColorOption> {
    @Override
    public Particle createParticle(ParticleColorOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      var particle = new RefreshParticle(level, type, x, y, z, xSpeed, ySpeed, zSpeed);
      particle.pickSprite(spriteSet);
      return particle;
    }
  }
}
