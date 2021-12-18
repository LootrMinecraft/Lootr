package noobanidus.mods.lootr.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import noobanidus.mods.lootr.world.processor.LootrChestProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(StructureTemplate.class)
public class MixinTemplate {
  @Inject(method = "processBlockInfos(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/template/PlacementSettings;Ljava/util/List;Lnet/minecraft/world/gen/feature/template/Template;)Ljava/util/List;", at = @At("HEAD"), remap = false)
  private static void processBlockInfos(LevelAccessor pLevel, BlockPos p_237145_1_, BlockPos p_237145_2_, StructurePlaceSettings pSettings, List<StructureTemplate.StructureBlockInfo> pBlockInfoList, @Nullable StructureTemplate template, CallbackInfoReturnable<List<StructureTemplate.StructureBlockInfo>> info) {
    if (!pSettings.getProcessors().contains(LootrChestProcessor.INSTANCE)) {
      pSettings.addProcessor(LootrChestProcessor.INSTANCE);
    }
  }
}
