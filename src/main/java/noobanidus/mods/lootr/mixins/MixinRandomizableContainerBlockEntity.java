package noobanidus.mods.lootr.mixins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraftforge.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.LootrAPI;
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

    if (incoming instanceof ILootBlockEntity) {
      return;
    }

    Level level = incoming.getLevel();

    if (level == null) {
      // This is over-protective because most instances of `setLootTable` are in structure post-processing which means the block entity should have already been promoted. In testing, specifically with `/lootr chest`, the block entity's level is already set by the time `setLootTable` is called so this is overprotective.
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server == null) {
        // We're probably on the client side so do nothing
        return;
      }

      // Otherwise, try to guess that we're on the overworld. This isn't ideal but I assume the majority of the time, this will be the overworld.
      level = server.overworld();
      LootrAPI.LOG.error("Block entity at {} had its loot table set before its level was set. It's not possible to determine its dimension, so presuming the overworld.", incoming.getBlockPos());
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
