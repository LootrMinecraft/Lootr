package noobanidus.mods.lootr.common.integration.sherdsapi;

import net.minecraft.resources.ResourceLocation;
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
  public static PotDecorationsAdapter getAdapterFrom(BlockEntity.DataComponentInput stack) {
    return SherdsIntegrationImpl.getAdapterFrom(stack);
  }

  @Nullable
  public static PotDecorationsAdapter getAdapterFrom(ItemStack stack) {
    return SherdsIntegrationImpl.getAdapterFrom(stack);
  }

  @Nullable
  public static ResourceLocation getCustomSideTexture(ItemStack item) {
    return SherdsIntegrationImpl.getCustomSideTexture(item);
  }
}
