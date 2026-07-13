package noobanidus.mods.lootr.common.mixin.integration.create;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import noobanidus.mods.lootr.common.integration.create.CreateIntegration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {"com.simibubi.create.content.schematics.SchematicPrinter"})
public class MixinSchematicPrinter {
  @SuppressWarnings("UnresolvedMixinReference")
  @Inject(method = "loadSchematic", at = @At("HEAD"))
  private void lootr$preventItemFrameConversionHead(ItemStack blueprint, Level originalWorld, boolean processNBT, CallbackInfo cir) {
    CreateIntegration.SKIP_ITEM_FRAMES = true;
  }

  @SuppressWarnings("UnresolvedMixinReference")
  @Inject(method = "loadSchematic", at = @At("RETURN"))
  private void lootr$preventItemFrameConversionTail(ItemStack blueprint, Level originalWorld, boolean processNBT, CallbackInfo cir) {
    CreateIntegration.SKIP_ITEM_FRAMES = false;
  }
}
