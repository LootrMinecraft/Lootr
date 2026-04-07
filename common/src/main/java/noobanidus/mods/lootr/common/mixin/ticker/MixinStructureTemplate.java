package noobanidus.mods.lootr.common.mixin.ticker;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.interfaces.accessor.ILootrItemFrameAccessor;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructureTemplate.class)
public class MixinStructureTemplate {
  @SuppressWarnings("UnresolvedMixinReference")
  @WrapOperation(method = {"lambda$placeEntities$0", "lambda$addEntitiesToWorld$0"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevelAccessor;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
  private static void lootr$AddEntitiesToWorldInject(ServerLevelAccessor level, Entity entity, Operation<Void> original) {
    if (LootrAPI.shouldConvertStructureItemFrames() && entity.is(LootrTags.Entity.CONVERT_ITEM_FRAMES) && !entity.entityTags()
        .contains(LootrConstants.CAN_CONVERT_TAG) /* CAN_CONVERT_TAG is handled elsewhere */) {
      ILootrItemFrameAccessor<Entity> adapter = LootrAPI.getItemFrameAccessor(entity);
      if (adapter != null && !adapter.isFixed(entity) && !adapter.isInvisible(entity)) {
        ItemStack contained = adapter.getItem(entity);
        if (!contained.isEmpty() && !contained.is(LootrTags.Items.ITEM_FRAME_CONVERT_BLACKLIST)) {
          LootrItemFrame newItemFrame = new LootrItemFrame(level.getLevel(), adapter.getPos(entity), adapter.getDirection(entity));
          newItemFrame.lootrSetItem(contained);
          PlatformAPI.copyEntityData(adapter, entity, newItemFrame);

          level.addFreshEntityWithPassengers(newItemFrame);
          return;
        }
      }
    }
    original.call(level, entity);
  }
}
