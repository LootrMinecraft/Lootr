package noobanidus.mods.lootr.common.mixin.accessor;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemFrame.class)
public interface AccessorMixinItemFrame {
  @Invoker("onItemChanged")
  void lootr$onItemChanged(ItemStack item);

  @Accessor("DATA_ITEM")
  static EntityDataAccessor<ItemStack> lootr$getDataItem() {
    throw new UnsupportedOperationException();
  }

  @Accessor("fixed")
  boolean lootr$isFixed();
}
