package noobanidus.mods.lootr.common.integration.sherdsapi.impl;

import dev.thomasglasser.sherdsapi.impl.StackPotDecorations;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.PotDecorationsAdapter;
import noobanidus.mods.lootr.common.api.registry.LootrProperties;
import org.jetbrains.annotations.Nullable;

public class SherdsIntegrationImpl {
  private static DataComponentType<StackPotDecorations> type = null;
  private static boolean checked = false;

  private static DataComponentType<ResourceLocation> type2 = null;
  private static boolean checked2 = false;

  @SuppressWarnings("unchecked")
  private static DataComponentType<StackPotDecorations> getSherdsDecorationsComponent() {
    if (!checked) {
      checked = true;
      var comp = BuiltInRegistries.DATA_COMPONENT_TYPE.get(LootrProperties.SHERDSAPI_POT_DECORATIONS);
      if (comp == null) {
        return null;
      }
      type = (DataComponentType<StackPotDecorations>) comp;
    }
    return type;
  }

  @SuppressWarnings("unchecked")
  private static DataComponentType<ResourceLocation> getSherdsTextureComponent() {
    if (!checked2) {
      checked2 = true;
      var comp = BuiltInRegistries.DATA_COMPONENT_TYPE.get(LootrProperties.SHERDSAPI_SHERD_PATTERN);
      if (comp == null) {
        return null;
      }
      type2 = (DataComponentType<ResourceLocation>) comp;
    }

    return type2;
  }

  public static PotDecorationsAdapter getAdapterFrom(BlockEntity.DataComponentInput stack) {
    DataComponentType<StackPotDecorations> sherdsType = getSherdsDecorationsComponent();
    if (sherdsType == null) {
      return null;
    }

    @Nullable StackPotDecorations decorations = stack.get(sherdsType);
    if (decorations == null) {
      return null;
    } else {
      return new PotDecorationsAdapter(decorations.ordered());
    }
  }

  public static PotDecorationsAdapter getAdapterFrom(ItemStack stack) {
    DataComponentType<StackPotDecorations> sherdsType = getSherdsDecorationsComponent();
    if (sherdsType == null) {
      return null;
    }

    if (!stack.has(sherdsType)) {
      return null;
    }

    @Nullable StackPotDecorations decorations = stack.get(sherdsType);
    if (decorations == null) {
      return null;
    } else {
      return new PotDecorationsAdapter(decorations.ordered());
    }
  }

  public static ResourceLocation getCustomSideTexture(ItemStack item) {
    DataComponentType<ResourceLocation> textureType = getSherdsTextureComponent();
    if (textureType == null) {
      return null;
    }

    if (!item.has(textureType)) {
      return null;
    }

    return item.get(textureType);
  }
}
