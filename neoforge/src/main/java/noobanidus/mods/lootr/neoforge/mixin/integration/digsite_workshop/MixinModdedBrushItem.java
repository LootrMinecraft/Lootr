package noobanidus.mods.lootr.neoforge.mixin.integration.digsite_workshop;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import noobanidus.mods.lootr.common.api.integration.IBrushable;
import noobanidus.mods.lootr.common.integration.digsite_workshop.IModdedBrushItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = {"net.tinkstav.digsite_workshop.item.ModdedBrushItem"})
public class MixinModdedBrushItem implements IModdedBrushItem {
  @Shadow
  private long brushingSpeed;

  @SuppressWarnings("UnresolvedMixinReference")
  @Definition(id = "BrushableBlockEntity", type = BrushableBlockEntity.class)
  @Expression("? instanceof BrushableBlockEntity")
  @ModifyExpressionValue(method = "onUseTick", at = @At("MIXINEXTRAS:EXPRESSION"))
  private boolean lootr$AllowBrushingOtherBlockEntities(boolean original, @Local(argsOnly = true, name = "arg1") Level level, @Local(argsOnly = true, name = "arg2") LivingEntity livingEntity, @Local(argsOnly = true, name = "arg3") ItemStack itemStack, @SuppressWarnings("LocalMayBeArgsOnly") @Local(name = "blockhitresult") BlockHitResult blockHitResult) {
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

  @Override
  public long lootr$getBrushingSpeed() {
    return brushingSpeed;
  }
}
