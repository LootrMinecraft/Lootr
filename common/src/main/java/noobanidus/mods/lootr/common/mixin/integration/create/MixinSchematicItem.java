package noobanidus.mods.lootr.common.mixin.integration.create;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import noobanidus.mods.lootr.common.integration.create.CreateIntegration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"com.simibubi.create.content.schematics.SchematicIcon"})
public class MixinSchematicItem {
  @SuppressWarnings("UnresolvedMixinReference")
  @Inject(method="loadSchematic", at=@At("HEAD"))
  private static void lootr$preventItemFrameConversionHead (Level level, ItemStack blueprint, CallbackInfoReturnable<StructureTemplate> cir) {
    CreateIntegration.SKIP_ITEM_FRAMES = true;
  }

  @SuppressWarnings("UnresolvedMixinReference")
  @Inject(method="loadSchematic", at=@At("TAIL"))
  private static void lootr$preventItemFrameConversionTail (Level level, ItemStack blueprint, CallbackInfoReturnable<StructureTemplate> cir) {
    CreateIntegration.SKIP_ITEM_FRAMES = false;
  }
}
