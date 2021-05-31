package noobanidus.mods.lootr.mixins;

import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import noobanidus.mods.lootr.world.processor.LootrChestProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(Template.class)
public class MixinTemplate {
  @Inject(method = "Lnet/minecraft/world/gen/feature/template/Template;processBlockInfos(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/template/PlacementSettings;Ljava/util/List;Lnet/minecraft/world/gen/feature/template/Template;)Ljava/util/List;", at = @At("HEAD"), remap = false)
  private static void processBlockInfos(IWorld p_237145_0_, BlockPos p_237145_1_, BlockPos p_237145_2_, PlacementSettings p_237145_3_, List<Template.BlockInfo> p_237145_4_, @Nullable Template template, CallbackInfoReturnable<List<Template.BlockInfo>> info) {
    if (!p_237145_3_.getProcessors().contains(LootrChestProcessor.INSTANCE)) {
      p_237145_3_.addProcessor(LootrChestProcessor.INSTANCE);
    }
  }
}
