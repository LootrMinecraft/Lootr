package noobanidus.mods.lootr.common.api.data;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.NotNull;

public class EmptyLootFiller implements LootFiller {
  public static final EmptyLootFiller INSTANCE = new EmptyLootFiller();

  @Override
  public void unpackLootTable(@NotNull ILootrInfoProvider provider, @NotNull Player player, Container inventory) {
    if (provider.isInfoReferenceInventory()) {
      LootrAPI.LOG.error("EmptyLootFiller was used to fill container {} with a reference inventory at {} in {}", provider.getInfoUUID(), provider.getInfoPos(), provider.getInfoDimension());
    }
  }
}
