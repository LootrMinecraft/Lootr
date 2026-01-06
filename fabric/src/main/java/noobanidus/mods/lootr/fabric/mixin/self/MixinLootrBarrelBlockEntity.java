package noobanidus.mods.lootr.fabric.mixin.self;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.common.block.entity.LootrBarrelBlockEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LootrBarrelBlockEntity.class)
public class MixinLootrBarrelBlockEntity implements RenderDataBlockEntity {
  @Override
  public @Nullable Object getRenderData() {
    Player player = ClientHooks.getPlayer();
    if (player == null) {
      return null;
    }

    return ((LootrBarrelBlockEntity) (Object)this).hasClientOpened(player.getUUID());
  }
}
