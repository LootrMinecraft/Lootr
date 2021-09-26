package noobanidus.mods.lootr.mixins;

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
  @Inject(method = "processBlockInfos(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/template/PlacementSettings;Ljava/util/List;Lnet/minecraft/world/gen/feature/template/Template;)Ljava/util/List;", at = @At("HEAD"), remap = false)
  private static void processBlockInfos(IWorld pLevel, BlockPos p_237145_1_, BlockPos p_237145_2_, PlacementSettings pSettings, List<Template.BlockInfo> pBlockInfoList, @Nullable Template template, CallbackInfoReturnable<List<Template.BlockInfo>> info) {
    if (!pSettings.getProcessors().contains(LootrChestProcessor.INSTANCE)) {
      pSettings.addProcessor(LootrChestProcessor.INSTANCE);
    }
  }
}
