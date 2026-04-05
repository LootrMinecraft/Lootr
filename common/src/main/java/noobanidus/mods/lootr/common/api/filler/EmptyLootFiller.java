package noobanidus.mods.lootr.common.api.filler;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import org.jetbrains.annotations.NotNull;

public class EmptyLootFiller implements ILootFiller {
  public static final EmptyLootFiller INSTANCE = new EmptyLootFiller();

  @Override
  public void unpackLootTable(@NotNull ILootrContainerInstance provider, @NotNull Player player, Container inventory) {
    if (provider.isDataReferenceInventory()) {
      LootrAPI.LOG.error("EmptyLootFiller was used to fill container {} with a reference inventory at {} in {}", provider.getDataId(), provider.getDataPos(), provider.getDataDimension());
    }
  }
}
