package noobanidus.mods.lootr.common.mixin.accessor;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FallingBlockEntity.class)
public interface AccessorMixinFallingBlockEntity {
  @Accessor("blockState")
  void lootr$setBlockState(BlockState state);
}
