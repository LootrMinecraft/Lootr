package noobanidus.mods.lootr.mixins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.block.entities.TileTicker;
import noobanidus.mods.lootr.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomizableContainerBlockEntity.class)
public class MixinRandomizableContainerBlockEntity {
  @Inject(method = "setLootTable(Lnet/minecraft/resources/ResourceLocation;J)V", at = @At("RETURN"))
  private void lootrOnSetLootTable(ResourceLocation p_59627_, long p_59628_, CallbackInfo ci) {
    if (ConfigManager.DISABLE.get() || !ConfigManager.AGGRESSIVE_MODE.get()) {
      return;
    }

    RandomizableContainerBlockEntity incoming = (RandomizableContainerBlockEntity) (Object) this;

    if (!incoming.hasLevel() || incoming instanceof ILootBlockEntity) {
      return;
    }

    Level level = incoming.getLevel();

    if (level == null) {
      return; // This should be false because `hasLevel`
    }

    if (level.isClientSide()) {
      return;
    }
    // By default block entities outside of the world border are
    // not converted. When the world border changes, you will
    // need to restart the server.
    if (ConfigManager.CHECK_WORLD_BORDER.get() && !level.getWorldBorder()
        .isWithinBounds(incoming.getBlockPos())) {
      return;
    }

    TileTicker.addEntry(incoming, level, incoming.getBlockPos());
  }
}
