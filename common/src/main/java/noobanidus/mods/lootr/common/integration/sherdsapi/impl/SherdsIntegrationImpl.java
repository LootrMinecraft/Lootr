package noobanidus.mods.lootr.common.integration.sherdsapi.impl;

import dev.thomasglasser.sherdsapi.impl.SherdsApi;
import dev.thomasglasser.sherdsapi.impl.StackPotDecorations;
import dev.thomasglasser.sherdsapi.impl.StackPotDecorationsHolder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.integration.decorated.PotDecorationsAdapter;
import org.jetbrains.annotations.Nullable;

public class SherdsIntegrationImpl {
  private static DataComponentType<?> type = null;
  private static boolean checked = false;

  private static DataComponentType<Identifier> type2 = null;
  private static boolean checked2 = false;

  @SuppressWarnings("unchecked")
  @Nullable
  private static DataComponentType<?> getSherdsDecorationsComponent() {
    if (!checked) {
      checked = true;
      var comp = BuiltInRegistries.DATA_COMPONENT_TYPE.get(LootrConstants.SHERDSAPI_POT_DECORATIONS);
      if (comp.isEmpty()) {
        return null;
      }
      var comp2 = comp.get();
      type = (DataComponentType<?>) comp2;
    }
    return type;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  private static DataComponentType<Identifier> getSherdsTextureComponent() {
    if (!checked2) {
      checked2 = true;
      var comp = BuiltInRegistries.DATA_COMPONENT_TYPE.get(LootrConstants.SHERDSAPI_SHERD_PATTERN);
      if (comp.isEmpty()) {
        return null;
      }
      var comp2 = comp.get();
      type2 = (DataComponentType<Identifier>) comp2;
    }

    return type2;
  }

  @Nullable
  public static PotDecorationsAdapter getAdapterFrom(DataComponentGetter stack) {
    DataComponentType<?> sherdsType = getSherdsDecorationsComponent();
    if (sherdsType == null) {
      return null;
    }

    @SuppressWarnings("unchecked") @Nullable StackPotDecorations decorations = stack.get((DataComponentType<StackPotDecorations>) sherdsType);
    if (decorations == null) {
      return null;
    } else {
      return new PotDecorationsAdapter(decorations.ordered());
    }
  }

  @Nullable
  public static PotDecorationsAdapter getAdapterFrom(ItemStack stack) {
    DataComponentType<?> sherdsType = getSherdsDecorationsComponent();
    if (sherdsType == null) {
      return null;
    }

    if (!stack.has(sherdsType)) {
      return null;
    }

    @SuppressWarnings("unchecked") @Nullable StackPotDecorations decorations = stack.get((DataComponentType<StackPotDecorations>) sherdsType);
    if (decorations == null) {
      return null;
    } else {
      return new PotDecorationsAdapter(decorations.ordered());
    }
  }

  @Nullable
  public static PotDecorationsAdapter getAdapterFrom(BlockEntity blockEntity) {
    if (!(blockEntity instanceof StackPotDecorationsHolder holderType)) {
      return null;
    }

    StackPotDecorations decorations = holderType.sherdsapi$getDecorations();
    if (decorations == null) {
      return null;
    } else {
      return new PotDecorationsAdapter(decorations.ordered());
    }
  }

  @Nullable
  public static Identifier getCustomSideTexture(ItemStack item) {
    DataComponentType<Identifier> textureType = getSherdsTextureComponent();
    if (textureType == null) {
      return null;
    }

    if (!item.has(textureType)) {
      return null;
    }

    return item.get(textureType);
  }
}
