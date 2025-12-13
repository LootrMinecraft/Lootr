package noobanidus.mods.lootr.fabric.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrDecoratedPotBlockEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;
import org.jetbrains.annotations.Nullable;

public class LootrFabricDecoratedPotBlockEntity extends LootrDecoratedPotBlockEntity {
  public LootrFabricDecoratedPotBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(LootrRegistry.getDecoratedPotBlockEntity(), blockPos, blockState);
  }

  @Override
  public @Nullable Object getRenderData() {
    Player player = ClientHooks.getPlayer();
    if (player == null) {
      return null;
    }

    return hasClientOpened(player.getUUID());
  }
}
