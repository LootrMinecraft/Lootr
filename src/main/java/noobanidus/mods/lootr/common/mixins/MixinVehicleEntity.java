package noobanidus.mods.lootr.common.mixins;

import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VehicleEntity.class)
public interface MixinVehicleEntity {
  @Invoker
  Item invokeGetDropItem();
}
