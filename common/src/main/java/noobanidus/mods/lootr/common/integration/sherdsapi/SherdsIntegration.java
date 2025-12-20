package noobanidus.mods.lootr.common.integration.sherdsapi;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.client.IPotDecorationsAdapter;
import noobanidus.mods.lootr.common.integration.sherdsapi.impl.SherdsIntegrationImpl;
import org.jetbrains.annotations.Nullable;

public class SherdsIntegration {
  @Nullable
  public static IPotDecorationsAdapter getAdapterFrom(BlockEntity.DataComponentInput stack) {
    return SherdsIntegrationImpl.getAdapterFrom(stack);
  }

  @Nullable
  public static IPotDecorationsAdapter getAdapterFrom(ItemStack stack) {
    return SherdsIntegrationImpl.getAdapterFrom(stack);
  }

  @Nullable
  public static ResourceLocation getCustomSideTexture(ItemStack item) {
    return SherdsIntegrationImpl.getCustomSideTexture(item);
  }
}
