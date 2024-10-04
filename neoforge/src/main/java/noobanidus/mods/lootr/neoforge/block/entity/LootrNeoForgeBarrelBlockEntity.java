package noobanidus.mods.lootr.neoforge.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import noobanidus.mods.lootr.common.block.entity.LootrBarrelBlockEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.neoforge.init.ModBlockProperties;
import org.jetbrains.annotations.NotNull;

public class LootrNeoForgeBarrelBlockEntity extends LootrBarrelBlockEntity {
  public LootrNeoForgeBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(pWorldPosition, pBlockState);
  }

  private static final ModelData OPENED_MODEL_DATA = ModelData.builder().with(ModBlockProperties.OPENED, true).build();
  private static final ModelData CLOSED_MODEL_DATA = ModelData.builder().with(ModBlockProperties.OPENED, false).build();

  @NotNull
  @Override
  public ModelData getModelData() {
    Player player = ClientHooks.getPlayer();
    if (player == null || !hasClientOpened(player.getUUID())) {
      return CLOSED_MODEL_DATA;
    } else {
      return OPENED_MODEL_DATA;
    }
  }
}
