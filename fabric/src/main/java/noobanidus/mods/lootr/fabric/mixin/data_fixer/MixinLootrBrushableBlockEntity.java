package noobanidus.mods.lootr.fabric.mixin.data_fixer;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.common.block.entity.LootrBrushableBlockEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LootrBrushableBlockEntity.class)
public class MixinLootrBrushableBlockEntity implements RenderDataBlockEntity {
  @Override
  public @Nullable Object getRenderData() {
    Player player = ClientHooks.getPlayer();
    if (player == null) {
      return null;
    }

    return ((LootrBrushableBlockEntity) (Object) this).hasClientOpened(player.getUUID());
  }
}
