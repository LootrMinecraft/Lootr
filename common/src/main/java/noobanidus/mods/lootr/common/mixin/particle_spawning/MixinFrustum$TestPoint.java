package noobanidus.mods.lootr.common.mixin.particle_spawning;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.client.FrustumExtension;
import org.joml.FrustumIntersection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Frustum.class)
public class MixinFrustum$TestPoint implements FrustumExtension {
  @Shadow
  @Final
  private FrustumIntersection intersection;

  @Shadow
  private double camX;

  @Shadow
  private double camY;

  @Shadow
  private double camZ;

  @Override
  public boolean lootr$isVisible(Vec3 point) {
    float newX = (float) ((float) point.x - this.camX);
    float newY = (float) ((float) point.y - this.camY);
    float newZ = (float) ((float) point.z - this.camZ);
    return this.intersection.testPoint(newX, newY, newZ);
  }
}
