package noobanidus.mods.lootr.neoforge.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrDecoratedPotBlockEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;
import org.jetbrains.annotations.NotNull;

public class LootrNeoForgeDecoratedPotBlockEntity extends LootrDecoratedPotBlockEntity {
  public LootrNeoForgeDecoratedPotBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(LootrRegistry.getDecoratedPotBlockEntity(), blockPos, blockState);
  }

  @NotNull
  @Override
  public ModelData getModelData() {
    Player player = ClientHooks.getPlayer();
    if (player == null || !hasClientOpened(player.getUUID())) {
      return ModelDataConstants.CLOSED_MODEL_DATA;
    } else {
      return ModelDataConstants.OPENED_MODEL_DATA;
    }
  }
}
