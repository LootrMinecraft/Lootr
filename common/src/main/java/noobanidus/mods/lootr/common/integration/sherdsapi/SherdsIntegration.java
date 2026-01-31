package noobanidus.mods.lootr.common.integration.sherdsapi;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.PotDecorationsAdapter;
import noobanidus.mods.lootr.common.integration.sherdsapi.impl.SherdsIntegrationImpl;
import org.jetbrains.annotations.Nullable;

public class SherdsIntegration {
  @Nullable
  public static PotDecorationsAdapter getAdapterFrom (BlockEntity blockEntity) {
    return SherdsIntegrationImpl.getAdapterFrom(blockEntity);
  }

  @Nullable
  public static PotDecorationsAdapter getAdapterFrom(DataComponentGetter stack) {
    return SherdsIntegrationImpl.getAdapterFrom(stack);
  }

  @Nullable
  public static PotDecorationsAdapter getAdapterFrom(ItemStack stack) {
    return SherdsIntegrationImpl.getAdapterFrom(stack);
  }

  @Nullable
  public static Identifier getCustomSideTexture(ItemStack item) {
    return SherdsIntegrationImpl.getCustomSideTexture(item);
  }
}
