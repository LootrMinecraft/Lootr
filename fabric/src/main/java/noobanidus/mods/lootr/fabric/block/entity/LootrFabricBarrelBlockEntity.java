package noobanidus.mods.lootr.fabric.block.entity;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.block.entity.LootrBarrelBlockEntity;
import noobanidus.mods.lootr.fabric.ClientHooks;
import org.jetbrains.annotations.Nullable;

public class LootrFabricBarrelBlockEntity extends LootrBarrelBlockEntity implements RenderDataBlockEntity {
  public LootrFabricBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(pWorldPosition, pBlockState);
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
