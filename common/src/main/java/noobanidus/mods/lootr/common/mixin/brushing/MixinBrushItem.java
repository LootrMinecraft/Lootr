package noobanidus.mods.lootr.common.mixin.brushing;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import noobanidus.mods.lootr.common.api.integration.IBrushable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BrushItem.class)
public class MixinBrushItem {
  @Definition(id = "BrushableBlockEntity", type = BrushableBlockEntity.class)
  @Expression("? instanceof BrushableBlockEntity")
  @ModifyExpressionValue(method = "onUseTick", at = @At("MIXINEXTRAS:EXPRESSION"))
  private boolean lootr$AllowBrushingOtherBlockEntities(boolean original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) LivingEntity livingEntity, @Local(argsOnly = true) ItemStack itemStack, @Local BlockHitResult blockHitResult) {
    BlockPos pos = blockHitResult.getBlockPos();
    if (level.getBlockEntity(pos) instanceof IBrushable brushable && livingEntity instanceof Player player) {
      boolean bl2 = brushable.IBrushable$brush(level.getGameTime(), player, blockHitResult.getDirection());
      if (bl2) {
        EquipmentSlot equipmentSlot = itemStack.equals(player.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
        itemStack.hurtAndBreak(1, player, equipmentSlot);
      }
      return false;
    }
    return original;
  }

/*
  Potential "solution" for Digsite Workbench's modded brush items in order
  to compensate for the improved speed of brushing without it having to
  override the entire `onUseTick` method in its derivative item class.

  @ModifyVariable(method="onUseTick", at=@At(value="STORE"), slice = @Slice(
      from = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/item/BrushItem;getUseDuration(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)I"
      ),
      to = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"
      )
  ), ordinal=0)
  private boolean lootr$ChangeBrushSpeed(boolean incoming, Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
    if (stack.getItem() instanceof ModdedBrushItem modded) {
      int i = stack.getUseDuration(entity) - remainingUseDuration + 1;
      return i % modded.getBrushingSpeed() == modded.getBrushingSpeed() / 2;
    }

    return incoming;
  }*/
}
