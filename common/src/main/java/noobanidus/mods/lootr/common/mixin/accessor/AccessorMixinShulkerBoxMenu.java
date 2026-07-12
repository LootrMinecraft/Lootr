package noobanidus.mods.lootr.common.mixin.accessor;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShulkerBoxMenu.class)
public interface AccessorMixinShulkerBoxMenu {
  @Accessor("container")
  Container lootr$getShulkerMenuContainer();
}
