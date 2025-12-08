package noobanidus.mods.lootr.common.mixins;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import noobanidus.mods.lootr.common.api.IBrushable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BrushItem.class)
public class MixinBrushItem {
  @Definition(id = "BrushableBlockEntity", type = BrushableBlockEntity.class)
  @Expression("? instanceof BrushableBlockEntity")
  @ModifyExpressionValue(method = "onUseTick", at = @At("MIXINEXTRAS:EXPRESSION"))
  private boolean lootr$AllowBrushingOtherBlockEntities(boolean original, @Local(argsOnly = true) Level level, @Local BlockHitResult blockHitResult, @Local Player player, @Local(argsOnly = true) ItemStack itemStack, @Local BlockEntity blockEntity) {
    if (blockEntity instanceof IBrushable brushable) {
      boolean bl2 = brushable.IBrushable$brush(level.getGameTime(), player, blockHitResult.getDirection());
      if (bl2) {
        EquipmentSlot equipmentSlot = itemStack.equals(player.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
        itemStack.hurtAndBreak(1, player, equipmentSlot);
      }
      return false;
    }
    return original;
  }
}
