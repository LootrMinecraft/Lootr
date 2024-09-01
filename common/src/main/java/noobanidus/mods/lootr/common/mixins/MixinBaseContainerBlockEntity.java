package noobanidus.mods.lootr.common.mixins;

import net.minecraft.core.NonNullList;
import net.minecraft.world.LockCode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BaseContainerBlockEntity.class)
public interface MixinBaseContainerBlockEntity {
  @Invoker
  NonNullList<ItemStack> invokeGetItems();

  @Accessor
  LockCode getLockKey();

  @Accessor
  void setLockKey (LockCode newKey);
}
